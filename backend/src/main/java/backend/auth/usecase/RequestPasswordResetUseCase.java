package backend.auth.usecase;

import backend.auth.model.entity.PasswordResetToken;
import backend.auth.port.EmailSenderPort;
import backend.auth.port.PasswordResetTokenPort;
import backend.auth.service.OpaqueTokenService;
import backend.user.model.entity.User;
import backend.user.model.valueObjects.Email;
import backend.user.port.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestPasswordResetUseCase {

    private final UserQueryPort userQueryPort;
    private final PasswordResetTokenPort passwordResetTokenPort;
    private final EmailSenderPort emailSenderPort;
    private final OpaqueTokenService opaqueTokenService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${auth.password-reset-expiration:900000}")
    private Long passwordResetExpirationMs;

    @Transactional
    public void execute(String email) {
        log.info("Solicitação de recuperação de senha para {}", email);

        // Não vazar existência do email
        userQueryPort.findByEmail(new Email(email)).ifPresent(this::createAndSendToken);
    }

    private void createAndSendToken(User user) {
        String rawToken = opaqueTokenService.generateToken();
        String tokenHash = opaqueTokenService.hash(rawToken);

        passwordResetTokenPort.save(PasswordResetToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(passwordResetExpirationMs)))
                .used(false)
                .build());

        String link = frontendUrl + "/reset-password?token=" + rawToken;
        String body = "Olá!\n\nClique no link para redefinir sua senha:\n" + link +
                "\n\nSe você não solicitou, ignore este email.";

        emailSenderPort.send(user.getEmail().getValue(), "Recuperação de Senha - GymApp", body);
    }
}

