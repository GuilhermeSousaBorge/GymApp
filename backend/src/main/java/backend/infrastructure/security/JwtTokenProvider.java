package backend.infrastructure.security;

import backend.user.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * CAMADA: INFRASTRUCTURE - Security
 *
 * Responsável por gerar e validar tokens JWT
 *
 * JWT (JSON Web Token):
 * - Header: algoritmo de criptografia
 * - Payload: dados do usuário (id, email, role)
 * - Signature: assinatura para validar autenticidade
 *
 * EXEMPLO DE TOKEN:
 * eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNjE2MjM5MDIyfQ.signature
 */
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;  // Chave secreta (vem do application.yml)

    @Value("${jwt.expiration}")
    private Long jwtExpiration;  // Tempo de expiração em ms (ex: 86400000 = 24h)

    @PostConstruct
    public void validateSecretLength() {
        if(jwtSecret == null || jwtSecret.isBlank()){
            throw new IllegalArgumentException("A chave secreta não pode ser nula ou vazia.");
        }
        int secretBytes = jwtSecret.getBytes(UTF_8).length;
        if (secretBytes < 32) {
            throw new IllegalArgumentException("A chave secreta deve ter pelo menos 32 caracteres para segurança adequada.");
        }
    }

    /**
     * Gera um token JWT para o usuário
     *
     * @param user usuário autenticado
     * @return token JWT string
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(user.getId().toString())           // ID do usuário
                .claim("role", user.getRole().getName())       // Nome da role
                .issuedAt(now)                              // Data de criação
                .expiration(expiryDate)                     // Data de expiração
                .signWith(getSigningKey())  // Assina com chave secreta
                .compact();
    }

    /**
     * Extrai o ID do usuário do token
     *
     * @param token JWT string
     * @return ID do usuário
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public String getRoleFromToken(String token){
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("role", String.class);
    }

    /**
     * Valida se o token é válido
     *
     * @param token JWT string
     * @return true se válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Token inválido, expirado ou malformado
            return false;
        }
    }

    /**
     * Gera a chave de assinatura baseada no secret
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
