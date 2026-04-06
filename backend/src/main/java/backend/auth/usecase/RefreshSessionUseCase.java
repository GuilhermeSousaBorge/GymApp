package backend.auth.usecase;

import backend.auth.dto.SessionResult;
import backend.auth.model.entity.AuthRefreshToken;
import backend.auth.port.RefreshTokenPort;
import backend.auth.service.OpaqueTokenService;
import backend.infrastructure.exception.UnauthorizedException;
import backend.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshSessionUseCase {

    private final RefreshTokenPort refreshTokenPort;
    private final OpaqueTokenService opaqueTokenService;
    private final CreateSessionUseCase createSessionUseCase;

    @Transactional
    public SessionResult execute(String refreshTokenRaw) {
        log.info("Solicitação de refresh token recebida");

        String tokenHash = opaqueTokenService.hash(refreshTokenRaw);
        AuthRefreshToken refreshToken = refreshTokenPort.findValidByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token inválido ou expirado"));

        refreshTokenPort.revoke(refreshToken);

        User user = refreshToken.getUser();
        if (!user.getActive()) {
            throw new UnauthorizedException("Usuário inativo");
        }

        return createSessionUseCase.execute(user);
    }
}

