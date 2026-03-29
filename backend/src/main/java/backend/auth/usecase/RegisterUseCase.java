package backend.auth.usecase;

import backend.auth.dto.LoginResponse;
import backend.auth.dto.RegisterRequest;
import backend.infrastructure.exception.BadRequestException;
import backend.infrastructure.security.JwtTokenProvider;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.Role;
import backend.user.model.entity.User;
import backend.user.model.valueObjects.Email;
import backend.user.model.valueObjects.Password;
import backend.user.port.RolePort;
import backend.user.port.UserCommandPort;
import backend.user.port.UserValidationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterUseCase {

    private final UserValidationPort userValidationPort;
    private final UserCommandPort userCommandPort;
    private final RolePort rolePort;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    @Transactional
    public LoginResponse execute(RegisterRequest request) {
        log.info("Tentativa de registro para email: {}", request.getEmail());

        Email email = new Email(request.getEmail());

        if (userValidationPort.existsByEmail(email)) {
            log.warn("Registro falhou: email já existe - {}", request.getEmail());
            throw new BadRequestException("Email já cadastrado");
        }

        Password passwordHash = Password.create(request.getPassword(), passwordEncoder);

        Role defaultRole = rolePort.findByName("Aluno")
                .orElseThrow(() -> new IllegalStateException("Role Aluno não encontrada"));

        User newUser = User.builder()
                .name(request.getName())
                .email(email)
                .passwordHash(passwordHash)
                .gender(request.getGender())
                .active(true)
                .role(defaultRole)
                .build();

        User savedUser = userCommandPort.save(newUser);

        log.info("Usuário registrado com sucesso: {} (ID {})",
                savedUser.getEmail(), savedUser.getId());

        String token = jwtTokenProvider.generateToken(savedUser);

        return LoginResponse.builder()
                .user(mapper.toResponse(savedUser))
                .token(token)
                .build();
    }
}
