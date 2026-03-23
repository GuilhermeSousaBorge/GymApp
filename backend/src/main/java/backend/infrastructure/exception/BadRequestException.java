package backend.infrastructure.exception;

/**
 * CAMADA: INFRASTRUCTURE - Exception
 *
 * Exceção lançada quando dados de entrada são inválidos
 * Ex: Email já cadastrado, dados obrigatórios faltando
 */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
