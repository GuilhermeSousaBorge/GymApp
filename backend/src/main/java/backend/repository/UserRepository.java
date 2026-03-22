package backend.repository;

import backend.model.entity.User;
import backend.model.valueObjects.Cpf;
import backend.model.valueObjects.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Spring Data JPA gera a query automaticamente baseado no nome do método
     * findByEmail → SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(Email email);

    /**
     * Verifica se existe usuário com determinado email
     * existsByEmail → SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(Email email);

    boolean existsByCpf(Cpf cpf);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = 'Aluno' AND u.active = true")
    int countByActiveTrue();

    int countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = 'Aluno' " +
            "AND u.active = true AND NOT EXISTS (SELECT p FROM TrainingProgram p WHERE p.student.id = u.id)")
    long countStudentsWithoutProgram();

    @Query("SELECT u FROM User u WHERE u.role.name = :role")
    List<User> findByRole(@Param("role") String roleName);

}
