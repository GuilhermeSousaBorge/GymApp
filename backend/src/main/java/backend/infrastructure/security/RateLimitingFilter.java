package backend.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CAMADA: INFRASTRUCTURE - Security
 *
 * Filtro de rate limiting para endpoints de autenticação.
 *
 * Protege contra ataques de força bruta (brute force) limitando
 * o número de requisições por IP para os endpoints /api/auth/*.
 *
 * Configuração: máximo de 5 tentativas por minuto por IP.
 * Ao exceder: retorna HTTP 429 Too Many Requests.
 *
 * Algoritmo: Token Bucket via Bucket4j.
 *   - Cada IP tem seu próprio bucket com 5 tokens.
 *   - Tokens são repostos a cada 1 minuto (greedy refill).
 */
@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    /** Máximo de requisições permitidas no intervalo. */
    private static final int MAX_REQUESTS = 5;

    /** Janela de tempo para reposição dos tokens. */
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);

    /** Cache em memória: IP → Bucket individual. */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Ignora preflight CORS e aplica limite apenas em endpoints de autenticação.
        if (HttpMethod.OPTIONS.matches(request.getMethod()) ||
                !request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = extractClientIp(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> buildBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit atingido para IP: {} em {}", clientIp, request.getRequestURI());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"error\":\"Too Many Requests\"," +
                    "\"message\":\"Limite de " + MAX_REQUESTS + " tentativas por minuto atingido. Tente novamente em instantes.\"}"
            );
        }
    }

    /**
     * Cria um novo bucket com a política de rate limiting configurada.
     */
    private Bucket buildBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(MAX_REQUESTS)
                .refillGreedy(MAX_REQUESTS, REFILL_DURATION)
                .build();
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Extrai o IP real do cliente, considerando proxies (X-Forwarded-For).
     */
    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // Primeiro IP da cadeia é o IP original do cliente
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

