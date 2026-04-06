package backend.auth.port;

import backend.auth.model.entity.EmailVerificationToken;

import java.util.Optional;

public interface EmailVerificationTokenPort {

    EmailVerificationToken save(EmailVerificationToken token);

    Optional<EmailVerificationToken> findValidByTokenHash(String tokenHash);

    void markUsed(EmailVerificationToken token);
}

