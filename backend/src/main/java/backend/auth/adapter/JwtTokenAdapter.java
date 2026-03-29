package backend.auth.adapter;

import backend.auth.port.TokenGeneratorPort;
import backend.auth.port.TokenParserPort;
import backend.auth.dto.LoginResponse;
import backend.infrastructure.security.JwtTokenProvider;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ADAPTADOR: JwtTokenProvider → Token Ports
 *
 * Implementa TokenGeneratorPort e TokenParserPort,
 * usando JwtTokenProvider como back-end.
 *
 * Benefício: JwtTokenProvider fica encapsulado. Se mudar para OAuth2,
 * apenas criar novo adaptador. AuthService não precisa saber.
 *
 * Padrão Adapter (Gang of Four)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenAdapter implements TokenGeneratorPort, TokenParserPort {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    
    // ========== TokenGeneratorPort Implementation ==========
    
    @Override
    public LoginResponse generateToken(User user) {
        log.debug("JwtTokenAdapter: generateToken for user {}", user.getId());
        String token = jwtTokenProvider.generateToken(user);
        return new LoginResponse(userMapper.toResponse(user), token);
    }
    
    @Override
    public LoginResponse generateToken(User user, Long expirationMs) {
        log.debug("JwtTokenAdapter: generateToken for user {} with custom expiration", user.getId());
        // Se JwtTokenProvider suportar expiração customizada, usar aqui
        // Senão, usar generateToken padrão
        String token = jwtTokenProvider.generateToken(user);
        return new LoginResponse(userMapper.toResponse(user), token);
    }
    
    // ========== TokenParserPort Implementation ==========
    
    @Override
    public Long extractUserId(String token) {
        log.debug("JwtTokenAdapter: extractUserId from token");
        return jwtTokenProvider.getUserIdFromToken(token);
    }
    
    @Override
    public String extractRole(String token) {
        log.debug("JwtTokenAdapter: extractRole from token");
        return jwtTokenProvider.getRoleFromToken(token);
    }
    
    @Override
    public boolean validateToken(String token) {
        log.debug("JwtTokenAdapter: validateToken");
        return jwtTokenProvider.validateToken(token);
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        log.debug("JwtTokenAdapter: isTokenExpired");
        // JwtTokenProvider já lida com expiração na validação
        // Se expirou, validateToken retorna false
        return !jwtTokenProvider.validateToken(token);
    }
}

