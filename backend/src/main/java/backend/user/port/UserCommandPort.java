package backend.user.port;

import backend.user.model.entity.User;

/**
 * PORTA: User Command (Escrita)
 *
 * Define contrato para operações de ESCRITA (save, update, delete) de usuários.
 * Inversão de dependência: Services dependem desta interface,
 * não da implementação concreta (UserRepository).
 *
 * Implementação: UserRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de ESCRITA
 * (leitura fica em UserQueryPort)
 */
public interface UserCommandPort {
    
    /**
     * Salva novo usuário
     * @param user Usuário a ser salvo
     * @return Usuário salvo (com ID gerado)
     */
    User save(User user);
    
    /**
     * Atualiza usuário existente
     * @param user Usuário com dados atualizados
     * @return Usuário atualizado
     */
    User update(User user);
    
    /**
     * Deleta usuário por ID
     * @param id ID do usuário a deletar
     */
    void deleteById(Long id);
    
    /**
     * Ativa/Desativa usuário
     * @param id ID do usuário
     * @param active Status desejado
     */
    void setActive(Long id, boolean active);
}

