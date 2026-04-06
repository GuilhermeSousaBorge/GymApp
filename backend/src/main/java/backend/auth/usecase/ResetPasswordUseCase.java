package backend.auth.usecase;

import backend.auth.model.entity.PasswordResetToken;
import backend.auth.port.PasswordResetTokenPort;
import backend.auth.service.OpaqueTokenService;
import backend.infrastructure.exception.BadRequestException;
import backend.user.model.entity.User;
import backend.user.model.valueObjects.Password;
import backend.user.port.UserCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResetPasswordUseCase {

    private final PasswordResetTokenPort passwordResetTokenPort;
    private final OpaqueTokenService opaqueTokenService;
    private final UserCommandPort userCommandPort;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(String tokenRaw, String newPassword) {
        log.info("Processando redefinição de senha");

        String tokenHash = opaqueTokenService.hash(tokenRaw);
        PasswordResetToken token = passwordResetTokenPort.findValidByTokenHash(tokenHash)
                .orElseThrow(() -> new BadRequestException("Token de reset inválido ou expirado"));

        User user = token.getUser();
        user.setPasswordHash(Password.create(newPassword, passwordEncoder));
        userCommandPort.update(user);

        passwordResetTokenPort.markUsed(token);
    }
}

