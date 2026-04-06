package backend.auth.usecase;

import backend.auth.dto.SessionResult;
import backend.auth.dto.RegisterRequest;
import backend.infrastructure.exception.BadRequestException;
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
    private final PasswordEncoder passwordEncoder;
    private final CreateSessionUseCase createSessionUseCase;


    @Transactional
    public SessionResult execute(RegisterRequest request) {
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
                .emailVerified(false)
                .role(defaultRole)
                .build();

        User savedUser = userCommandPort.save(newUser);

        log.info("Usuário registrado com sucesso: {} (ID {})",
                savedUser.getEmail(), savedUser.getId());

        return createSessionUseCase.execute(savedUser);
    }
}
