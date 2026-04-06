package backend.auth.usecase;

import backend.auth.dto.SessionResult;
import backend.auth.model.entity.AuthRefreshToken;
import backend.auth.port.RefreshTokenPort;
import backend.auth.service.OpaqueTokenService;
import backend.infrastructure.security.JwtTokenProvider;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
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
public class CreateSessionUseCase {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final OpaqueTokenService opaqueTokenService;
    private final RefreshTokenPort refreshTokenPort;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshTokenExpirationMs;

    @Transactional
    public SessionResult execute(User user) {
        log.info("Criando sessão para usuário {}", user.getId());

        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshTokenRaw = opaqueTokenService.generateToken();
        String refreshTokenHash = opaqueTokenService.hash(refreshTokenRaw);

        refreshTokenPort.revokeAllByUser(user);
        refreshTokenPort.save(AuthRefreshToken.builder()
                .user(user)
                .tokenHash(refreshTokenHash)
                .expiresAt(LocalDateTime.now().plus(Duration.ofMillis(refreshTokenExpirationMs)))
                .revoked(false)
                .build());

        return SessionResult.builder()
                .user(userMapper.toResponse(user))
                .accessToken(accessToken)
                .refreshToken(refreshTokenRaw)
                .build();
    }
}

