package backend.auth.usecase;

import backend.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidateTokenUseCase {

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public boolean execute(String token){
        log.info("Validando token JWT");
        return jwtTokenProvider.validateToken(token);
    }
}
