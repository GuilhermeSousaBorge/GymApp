package backend.auth.adapter;

import backend.auth.model.entity.EmailVerificationToken;
import backend.auth.port.EmailVerificationTokenPort;
import backend.auth.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailVerificationTokenRepositoryAdapter implements EmailVerificationTokenPort {

    private final EmailVerificationTokenRepository repository;

    @Override
    public EmailVerificationToken save(EmailVerificationToken token) {
        return repository.save(token);
    }

    @Override
    public Optional<EmailVerificationToken> findValidByTokenHash(String tokenHash) {
        return repository.findByTokenHashAndUsedFalseAndExpiresAtAfter(tokenHash, LocalDateTime.now());
    }

    @Override
    public void markUsed(EmailVerificationToken token) {
        token.setUsed(true);
        repository.save(token);
    }
}

