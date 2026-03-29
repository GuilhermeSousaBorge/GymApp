package backend.user.port;

import backend.user.model.valueObjects.Cpf;
import backend.user.model.valueObjects.Email;

/**
 * PORTA: User Validation (Validação)
 *
 * Define contrato para operações de VALIDAÇÃO de usuários.
 * Responsável por verificar existência de usuários (com queries específicas).
 * 
 * Implementação: UserRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de VALIDAÇÃO/EXISTÊNCIA
 * (lógica separada de query e command para responsabilidades claras)
 */
public interface UserValidationPort {
    
    /**
     * Verifica se email já existe
     * @param email Email a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByEmail(Email email);

    boolean existsByEmailAndIdNot(Email email, Long id);
    
    /**
     * Verifica se CPF já existe
     * @param cpf CPF a verificar
     * @return true se existe, false caso contrário
     */
    boolean existsByCpf(Cpf cpf);

    boolean existsByCpfAndIdNot(Cpf cpf, Long id);
    
    /**
     * Conta estudantes sem programa de treinamento
     * @return Quantidade de estudantes sem programa
     */
    long countStudentsWithoutProgram();
}

