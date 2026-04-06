package backend.auth.repository;

import backend.auth.model.entity.AuthRefreshToken;
import backend.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AuthRefreshTokenRepository extends JpaRepository<AuthRefreshToken, Long> {

    Optional<AuthRefreshToken> findByTokenHashAndRevokedFalseAndExpiresAtAfter(String tokenHash, LocalDateTime now);

    void deleteByUser(User user);
}

