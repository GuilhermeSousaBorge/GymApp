package backend.auth.port;

import backend.auth.model.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenPort {

    PasswordResetToken save(PasswordResetToken token);

    Optional<PasswordResetToken> findValidByTokenHash(String tokenHash);

    void markUsed(PasswordResetToken token);
}

