package backend.user.port;

import backend.user.model.entity.Role;
import backend.user.model.enums.Roles;

import java.util.Optional;

/**
 * PORTA: Role Port
 *
 * Define contrato para operações de LEITURA de Roles.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: RoleRepositoryAdapter
 *
 * Princípio: Interface Segregation - interface pequena e específica
 */
public interface RolePort {

    Optional<Role> findById(Long id);
    
    /**
     * Busca role por enum
     * @param role Enum do role (ALUNO, PERSONAL_TRAINER, ADMINISTRADOR)
     * @return Optional contendo role ou vazio
     */
    Optional<Role> findByName(Roles role);
    
    /**
     * Busca role por nome string
     * @param name Nome do role
     * @return Optional contendo role ou vazio
     */
    Optional<Role> findByName(String name);
}

