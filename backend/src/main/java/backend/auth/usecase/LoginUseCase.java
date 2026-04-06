package backend.auth.usecase;

import backend.auth.dto.LoginRequest;
import backend.auth.dto.SessionResult;
import backend.infrastructure.exception.UnauthorizedException;
import backend.user.model.entity.User;
import backend.user.model.valueObjects.Email;
import backend.user.port.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserQueryPort userQueryPort;
    private final PasswordEncoder passwordEncoder;
    private final CreateSessionUseCase createSessionUseCase;

    @Transactional
    public SessionResult execute(LoginRequest request){
        log.info("Tentativa de login para email: {}", request.getEmail());

        Email email = new Email(request.getEmail());

        User user = userQueryPort.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login falhou: email não encontrado - {}", request.getEmail());
                    return new UnauthorizedException("Email ou senha inválidos");
                });

        if (!user.getActive()) {
            log.warn("Login falhou: usuário inativo - {}", request.getEmail());
            throw new UnauthorizedException("Usuário inativo");
        }

        if (!user.isPasswordValid(request.getPassword(), passwordEncoder)) {
            log.warn("Login falhou: senha incorreta - {}", request.getEmail());
            throw new UnauthorizedException("Email ou senha inválidos");
        }

        log.info("Login bem-sucedido: {}", request.getEmail());
        return createSessionUseCase.execute(user);
    }
}
