package backend.infrastructure.exception;

/**
 * CAMADA: INFRASTRUCTURE - Exception
 *
 * Exceção lançada quando credenciais são inválidas
 * Será capturada pelo GlobalExceptionHandler
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
