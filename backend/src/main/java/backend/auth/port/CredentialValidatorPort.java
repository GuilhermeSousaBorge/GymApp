package backend.auth.port;

import backend.user.model.valueObjects.Email;
import backend.user.model.valueObjects.Password;

/**
 * PORTA: Credential Validator
 *
 * Define contrato para VALIDAÇÃO de credenciais.
 * Responsável por validar email/password durante login.
 *
 * Implementação: CredentialValidatorAdapter
 *
 * Princípio: Dependency Inversion - abstração, Single Responsibility - apenas validação
 */
public interface CredentialValidatorPort {
    
    /**
     * Valida credenciais de login
     * @param email Email do usuário
     * @param rawPassword Senha em plain text
     * @return true se credenciais são válidas, false caso contrário
     */
    boolean validateCredentials(Email email, String rawPassword);
}

