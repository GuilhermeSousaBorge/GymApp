package backend.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * CAMADA: INFRASTRUCTURE - Exception Handler
 *
 * Captura TODAS as exceções da aplicação e retorna respostas padronizadas
 *
 * @RestControllerAdvice: Intercepta exceções de todos os controllers
 *
 * POR QUÊ?
 * - Centraliza tratamento de erros
 * - Evita try-catch em cada controller
 * - Retorna mensagens consistentes para o frontend
 * - Facilita logging e monitoramento
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Trata exceções de validação (@Valid)
     *
     * Retorna mapa com campo → mensagem de erro
     *
     * EXEMPLO:
     * {
     *   "timestamp": "2026-01-28T10:00:00",
     *   "status": 400,
     *   "errors": {
     *     "email": "Email é obrigatório",
     *     "password": "Senha deve ter no mínimo 6 caracteres"
     *   }
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        log.warn("Erro de validação: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();

        // Extrai erros de cada campo
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Trata UnauthorizedException (login inválido)
     *
     * EXEMPLO:
     * {
     *   "timestamp": "2026-01-28T10:00:00",
     *   "status": 401,
     *   "message": "Email ou senha inválidos"
     * }
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(
            UnauthorizedException ex) {

        log.warn("Erro de autenticação: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    /**
     * Trata BadRequestException (email duplicado, etc)
     *
     * EXEMPLO:
     * {
     *   "timestamp": "2026-01-28T10:00:00",
     *   "status": 400,
     *   "message": "Email já cadastrado"
     * }
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequestException(
            BadRequestException ex) {

        log.warn("Erro de requisição: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Trata qualquer exceção não esperada
     *
     * Evita expor detalhes internos ao cliente
     *
     * EXEMPLO:
     * {
     *   "timestamp": "2026-01-28T10:00:00",
     *   "status": 500,
     *   "message": "Erro interno do servidor"
     * }
     */

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex) {

        log.warn("Acesso negado: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.FORBIDDEN.value());
        response.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {

        log.error("Erro inesperado: ", ex);  // Log completo com stack trace

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("message", "Erro interno do servidor");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
