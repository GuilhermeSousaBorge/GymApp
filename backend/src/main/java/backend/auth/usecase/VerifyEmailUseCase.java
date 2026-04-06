package backend.auth.usecase;

import backend.auth.model.entity.EmailVerificationToken;
import backend.auth.port.EmailVerificationTokenPort;
import backend.auth.service.OpaqueTokenService;
import backend.infrastructure.exception.BadRequestException;
import backend.user.model.entity.User;
import backend.user.port.UserCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyEmailUseCase {

    private final EmailVerificationTokenPort emailVerificationTokenPort;
    private final OpaqueTokenService opaqueTokenService;
    private final UserCommandPort userCommandPort;

    @Transactional
    public void execute(String tokenRaw) {
        log.info("Processando verificação de email");

        String tokenHash = opaqueTokenService.hash(tokenRaw);
        EmailVerificationToken token = emailVerificationTokenPort.findValidByTokenHash(tokenHash)
                .orElseThrow(() -> new BadRequestException("Token de verificação inválido ou expirado"));

        User user = token.getUser();
        user.setEmailVerified(true);
        userCommandPort.update(user);

        emailVerificationTokenPort.markUsed(token);
    }
}

