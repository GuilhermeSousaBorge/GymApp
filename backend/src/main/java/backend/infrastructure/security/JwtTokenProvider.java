package backend.infrastructure.security;

import backend.user.model.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

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
                .setSubject(user.getId().toString())           // ID do usuário
                .claim("role", user.getRole().getName())       // Nome da role
                .setIssuedAt(now)                              // Data de criação
                .setExpiration(expiryDate)                     // Data de expiração
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // Assina com chave secreta
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
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public String getRoleFromToken(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

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
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // Token inválido, expirado ou malformado
            return false;
        }
    }

    /**
     * Gera a chave de assinatura baseada no secret
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
