package backend.user.repository;

import backend.user.model.entity.User;
import backend.user.model.valueObjects.Cpf;
import backend.user.model.valueObjects.Email;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    boolean existsByEmailAndIdNot(Email email, Long id);

    boolean existsByCpf(Cpf cpf);
    boolean existsByCpfAndIdNot(Cpf cpf, Long id);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = 'Aluno' AND u.active = true")
    int countByActiveTrue();

    int countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = 'Aluno' " +
            "AND u.active = true AND NOT EXISTS (SELECT p FROM TrainingProgram p WHERE p.student.id = u.id)")
    long countStudentsWithoutProgram();

    @Query("SELECT u FROM User u WHERE u.role.name = :role")
    List<User> findByRole(@Param("role") String roleName);

    @Query("SELECT u FROM User u WHERE u.role.name = 'Aluno' AND u.active = true ORDER BY u.createdAt DESC")
    List<User> findLatestStudents(Pageable pageable);

}
