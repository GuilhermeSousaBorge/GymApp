package backend.auth.port;

import backend.auth.model.entity.AuthRefreshToken;
import backend.user.model.entity.User;

import java.util.Optional;

public interface RefreshTokenPort {

    AuthRefreshToken save(AuthRefreshToken refreshToken);

    Optional<AuthRefreshToken> findValidByTokenHash(String tokenHash);

    void revoke(AuthRefreshToken refreshToken);

    void revokeAllByUser(User user);
}

