package backend.auth.port;

import backend.auth.dto.LoginResponse;
import backend.user.model.entity.User;

/**
 * PORTA: Token Generator
 *
 * Define contrato para GERAÇÃO de tokens JWT.
 * Inversão de dependência: AuthService depende desta interface,
 * não de JwtTokenProvider concreto.
 *
 * Implementação: JwtTokenAdapter
 *
 * Benefício: Se mudar de JWT para OAuth2, apenas alterar implementação.
 *
 * Princípio: Dependency Inversion - abstração antes de implementação concreta
 */
public interface TokenGeneratorPort {
    
    /**
     * Gera token JWT para usuário
     * @param user Usuário autenticado
     * @return Response contendo token e dados do usuário
     */
    LoginResponse generateToken(User user);
    
    /**
     * Gera token com expiração customizada
     * @param user Usuário autenticado
     * @param expirationMs Expiração em milissegundos
     * @return Response contendo token e dados do usuário
     */
    LoginResponse generateToken(User user, Long expirationMs);
}

