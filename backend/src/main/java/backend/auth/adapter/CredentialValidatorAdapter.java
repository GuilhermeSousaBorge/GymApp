package backend.auth.adapter;

import backend.auth.port.CredentialValidatorPort;
import backend.user.port.UserQueryPort;
import backend.user.model.entity.User;
import backend.user.model.valueObjects.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * ADAPTADOR: Credential Validation
 *
 * Implementa CredentialValidatorPort.
 * Responsável por validar credenciais (email + password).
 *
 * Padrão Adapter + Strategy
 */
@Component
@Slf4j
public class CredentialValidatorAdapter implements CredentialValidatorPort {
    
    private final UserQueryPort userQueryPort;
    private final PasswordEncoder passwordEncoder;

    public CredentialValidatorAdapter(UserQueryPort userQueryPort, PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
        this.userQueryPort = userQueryPort;
    }
    
    @Override
    public boolean validateCredentials(Email email, String rawPassword) {
        log.debug("CredentialValidatorAdapter: validateCredentials for email {}", email.getValue());
        
        // Busca usuário por email
        var userOpt = userQueryPort.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            log.warn("CredentialValidatorAdapter: user not found for email {}", email.getValue());
            return false;
        }
        
        User user = userOpt.get();
        
        // Verifica se usuário está ativo
        if (!user.getActive()) {
            log.warn("CredentialValidatorAdapter: user {} is inactive", user.getId());
            return false;
        }
        
        // Compara senha com hash usando método da entidade
        boolean passwordMatches = user.isPasswordValid(rawPassword, passwordEncoder);
        
        if (!passwordMatches) {
            log.warn("CredentialValidatorAdapter: invalid password for user {}", user.getId());
        }
        
        return passwordMatches;
    }
}

