package backend.auth.port;

/**
 * PORTA: Token Parser
 *
 * Define contrato para PARSING e VALIDAÇÃO de tokens JWT.
 * Responsável por extrair informações do token.
 *
 * Implementação: JwtTokenAdapter
 *
 * Princípio: Dependency Inversion - abstração antes de implementação
 */
public interface TokenParserPort {
    
    /**
     * Extrai ID do usuário do token
     * @param token Token JWT
     * @return ID do usuário
     */
    Long extractUserId(String token);
    
    /**
     * Extrai role do usuário do token
     * @param token Token JWT
     * @return Role do usuário
     */
    String extractRole(String token);
    
    /**
     * Valida se token é válido
     * @param token Token JWT
     * @return true se válido, false caso contrário
     */
    boolean validateToken(String token);
    
    /**
     * Verifica se token expirou
     * @param token Token JWT
     * @return true se expirado, false caso contrário
     */
    boolean isTokenExpired(String token);
}

