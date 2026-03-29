package backend.user.port;

import backend.user.model.entity.User;
import backend.user.model.valueObjects.Email;

import java.util.List;
import java.util.Optional;

/**
 * PORTA: User Query (Leitura)
 *
 * Define contrato para operações de LEITURA de usuários.
 * Inversão de dependência: Controllers/Services dependem desta interface,
 * não da implementação concreta (UserRepository).
 *
 * Implementação: UserRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de LEITURA
 * (escrita fica em UserCommandPort)
 */
public interface UserQueryPort {
    
    /**
     * Busca usuário por ID
     * @param id ID do usuário
     * @return Optional contendo usuário ou vazio
     */
    Optional<User> findById(Long id);
    
    /**
     * Busca usuário por email
     * @param email Email do usuário (Value Object)
     * @return Optional contendo usuário ou vazio
     */
    Optional<User> findByEmail(Email email);
    
    /**
     * Lista todos os usuários ativos
     * @return Lista de usuários
     */
    List<User> findAll();
    
    /**
     * Busca usuários por role/perfil
     * @param roleName Nome do role (ex: "Aluno", "PersonalTrainer", "Administrador")
     * @return Lista de usuários com esse role
     */
    List<User> findByRole(String roleName);
    
    /**
     * Conta total de usuários ativos
     * @return Quantidade de usuários ativos
     */
    int countActive();
    
    /**
     * Conta usuários ativos criados em intervalo de datas
     * @param startDate Data de início
     * @param endDate Data de término
     * @return Quantidade de usuários
     */
    int countCreatedBetween(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}

