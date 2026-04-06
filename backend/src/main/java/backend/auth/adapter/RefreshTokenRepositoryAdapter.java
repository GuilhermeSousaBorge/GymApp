package backend.auth.adapter;

import backend.auth.model.entity.AuthRefreshToken;
import backend.auth.port.RefreshTokenPort;
import backend.auth.repository.AuthRefreshTokenRepository;
import backend.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenPort {

    private final AuthRefreshTokenRepository repository;

    @Override
    public AuthRefreshToken save(AuthRefreshToken refreshToken) {
        return repository.save(refreshToken);
    }

    @Override
    public Optional<AuthRefreshToken> findValidByTokenHash(String tokenHash) {
        return repository.findByTokenHashAndRevokedFalseAndExpiresAtAfter(tokenHash, LocalDateTime.now());
    }

    @Override
    public void revoke(AuthRefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        repository.save(refreshToken);
    }

    @Override
    public void revokeAllByUser(User user) {
        repository.deleteByUser(user);
    }
}

