package backend.auth.adapter;

import backend.auth.model.entity.PasswordResetToken;
import backend.auth.port.PasswordResetTokenPort;
import backend.auth.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryAdapter implements PasswordResetTokenPort {

    private final PasswordResetTokenRepository repository;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        return repository.save(token);
    }

    @Override
    public Optional<PasswordResetToken> findValidByTokenHash(String tokenHash) {
        return repository.findByTokenHashAndUsedFalseAndExpiresAtAfter(tokenHash, LocalDateTime.now());
    }

    @Override
    public void markUsed(PasswordResetToken token) {
        token.setUsed(true);
        repository.save(token);
    }
}

