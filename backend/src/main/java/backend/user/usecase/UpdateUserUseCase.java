package backend.user.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.user.dto.UserResponse;
import backend.user.dto.UserUpdateRequest;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.Address;
import backend.user.model.entity.Role;
import backend.user.model.entity.User;
import backend.user.model.valueObjects.Cpf;
import backend.user.model.valueObjects.Email;
import backend.user.port.RolePort;
import backend.user.port.UserCommandPort;
import backend.user.port.UserQueryPort;
import backend.user.port.UserValidationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UpdateUserUseCase {

    private final UserQueryPort userQueryPort;
    private final UserCommandPort userCommandPort;
    private final UserValidationPort userValidationPort;
    private final RolePort rolePort;
    private final UserMapper mapper;

    public UpdateUserUseCase(UserQueryPort userQueryPort, UserCommandPort userCommandPort,
                             UserValidationPort userValidationPort, RolePort rolePort,
                             UserMapper mapper) {
        this.userQueryPort = userQueryPort;
        this.userCommandPort = userCommandPort;
        this.userValidationPort = userValidationPort;
        this.rolePort = rolePort;
        this.mapper = mapper;
    }

    @Transactional
    public UserResponse execute(Long id, UserUpdateRequest userRequest) {
        log.info("Atualizando usuário com ID: {}", id);
        
        // Step 1: Buscar usuário existente
        User user = userQueryPort.findById(id)
            .orElseThrow(() -> new BadRequestException("Usuário não encontrado"));
        
        // Step 2: Validar e atualizar Email (se fornecido)
        if(userRequest.getEmail() != null) {
            Email email = new Email(userRequest.getEmail());
            if (userValidationPort.existsByEmailAndIdNot(email, id)) {
                throw new BadRequestException("Email já cadastrado");
            }
            user.setEmail(email);
        }

        // Step 3: Validar e atualizar CPF (se fornecido)
        if(userRequest.getCpf() != null) {
            Cpf cpf = new Cpf(userRequest.getCpf());
            if (userValidationPort.existsByCpfAndIdNot(cpf, id)) {
                throw new BadRequestException("CPF já cadastrado");
            }
            user.setCpf(cpf);
        }

        // Step 4: Atualizar campos básicos via updateForm
        user.updateForm(userRequest);

        // Step 5: Atualizar Role (se fornecido)
        if (userRequest.getRoleId() != null) {
            Role role = rolePort.findById(userRequest.getRoleId())
                    .orElseThrow(() -> new BadRequestException("Role inválida"));
            user.setRole(role);
        }

        // Step 6: Atualizar Address (se fornecido)
        if (userRequest.getAddress() != null) {
            Address address = user.getAddress();
            if (address == null) {
                address = new Address();
            }
            address.updateFrom(userRequest.getAddress());
            user.setAddress(address);
        }

        // Step 7: Persistir e retornar
        User savedUser = userCommandPort.update(user);
        
        log.info("Usuário com ID: {} atualizado com sucesso", id);

        return mapper.toResponse(savedUser);
    }
}
