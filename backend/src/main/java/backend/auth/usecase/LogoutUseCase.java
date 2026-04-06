package backend.auth.usecase;

import backend.auth.model.entity.AuthRefreshToken;
import backend.auth.port.RefreshTokenPort;
import backend.auth.service.OpaqueTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogoutUseCase {

    private final RefreshTokenPort refreshTokenPort;
    private final OpaqueTokenService opaqueTokenService;

    @Transactional
    public void execute(String refreshTokenRaw) {
        log.info("Solicitação de logout recebida");

        String tokenHash = opaqueTokenService.hash(refreshTokenRaw);
        refreshTokenPort.findValidByTokenHash(tokenHash).ifPresent(this::revokeToken);
    }

    private void revokeToken(AuthRefreshToken token) {
        refreshTokenPort.revoke(token);
    }
}

