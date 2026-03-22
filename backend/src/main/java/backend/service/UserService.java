package backend.service;

import backend.dto.request.user.UserRequest;
import backend.dto.request.user.UserUpdateRequest;
import backend.dto.response.user.UserResponse;
import backend.infrastructure.exception.BadRequestException;
import backend.infrastructure.security.JwtTokenProvider;
import backend.mapper.UserMapper;
import backend.model.entity.Address;
import backend.model.entity.Role;
import backend.model.entity.User;
import backend.model.enums.Roles;
import backend.model.valueObjects.Cpf;
import backend.model.valueObjects.Email;
import backend.repository.RoleRepository;
import backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Buscando usuário por ID: {}", id);

        User user = getUser(id);

        return userMapper.toResponse(user);
    }

    public List<UserResponse> listUsers(Authentication authentication) {
        log.info("Listando usuários...");
        // Lógica para listar usuários

        String token = (String) authentication.getCredentials();
        String roleFromToken = jwtTokenProvider.getRoleFromToken(token);

        List<User> users = new ArrayList<>();
        if(roleFromToken.equals(Roles.ALUNO.getRole())){
           users = userRepository.findByRole(Roles.PERSONAL.getRole());
        }else{
            users = userRepository.findAll();
        }

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest userRequest) {
        log.info("Atualizando usuário com ID: {}", id);
        // Lógica para atualizar usuário
        User user = getUser(id);
        Email email = new Email(userRequest.getEmail());
        Cpf cpf = new Cpf(userRequest.getCpf());

        if(userRequest.getEmail() != null && !user.getEmail().equals(email)){
            if(userRepository.existsByEmail(email)){
                throw new BadRequestException("Email já cadastrado");
            }
        }

        if(userRequest.getCpf() != null && !Objects.equals(user.getCpf(), cpf)){
            if(userRepository.existsByCpf(cpf)){
                throw new BadRequestException("CPF já cadastrado");
            }
        }

        user.updateForm(userRequest);

        if (userRequest.getRoleId() != null) {
            Role role = roleRepository.findById(userRequest.getRoleId())
                    .orElseThrow(() -> new BadRequestException("Role inválida"));
            user.setRole(role);
        }

        if (userRequest.getAddress() != null) {
            Address address = user.getAddress() != null ? user.getAddress() : new Address();
            user.setAddress(address);
            address.updateFrom(userRequest.getAddress());
        }

        userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deletando usuário com ID: {}", id);
        User user = getUser(id);
        userRepository.delete(user);
    }

    @Transactional
    public void deactivateUser(Long id) {
        log.info("Deletando usuário com ID: {}", id);

        User user = getUser(id);

        user.setActive(false);

        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long id) {
        log.info("Ativando usuário com ID: {}", id);

        User user = getUser(id);

        user.setActive(true);

        userRepository.save(user);
    }


    private User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> {
            log.warn("Usuário não encontrado para ID: {}", id);
            return new BadRequestException("Usuário não encontrado");
        });
    }
}
