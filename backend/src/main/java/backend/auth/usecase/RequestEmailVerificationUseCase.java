package backend.auth.usecase;

import backend.auth.model.entity.EmailVerificationToken;
import backend.auth.port.EmailSenderPort;
import backend.auth.port.EmailVerificationTokenPort;
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
public class RequestEmailVerificationUseCase {

    private final UserQueryPort userQueryPort;
    private final EmailVerificationTokenPort emailVerificationTokenPort;
    private final EmailSenderPort emailSenderPort;
    private final OpaqueTokenService opaqueTokenService;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${auth.email-verification-expiration:900000}")
    private Long emailVerificationExpirationMs;

    @Transactional
    public void execute(String email) {
        log.info("Solicitação de verificação de email para {}", email);

        userQueryPort.findByEmail(new Email(email)).ifPresent(this::createAndSendToken);
    }

    private void createAndSendToken(User user) {
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            return;
        }

        String rawToken = opaqueTokenService.generateToken();
        String tokenHash = opaqueTokenService.hash(rawToken);

        emailVerificationTokenPort.save(EmailVerificationToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(emailVerificationExpirationMs)))
                .used(false)
                .build());

        String link = frontendUrl + "/verify-email?token=" + rawToken;
        String body = "Olá!\n\nClique no link para verificar seu email:\n" + link +
                "\n\nSe você não solicitou, ignore este email.";

        emailSenderPort.send(user.getEmail().getValue(), "Verificação de Email - GymApp", body);
    }
}

