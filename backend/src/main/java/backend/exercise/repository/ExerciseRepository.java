package backend.exercise.repository;

import backend.exercise.model.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByActiveTrue();

    List<Exercise> findByCategoryId(Long categoryId);

    List<Exercise> findByCategoryIdAndActiveTrue(Long categoryId);

    boolean existsByName(String name);

    /**
     * Busca exercícios por nome ou equipamento
     */
    @Query("""
        SELECT e FROM Exercise e
        WHERE e.active = true
        AND (
            LOWER(e.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
            OR LOWER(e.equipment) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        )
    """)
    List<Exercise> searchExercises(@Param("searchTerm") String searchTerm);

    /**
     * Busca exercício com categoria carregada
     */
    @Query("""
        SELECT e FROM Exercise e
        JOIN FETCH e.category
        WHERE e.id = :id
    """)
    Optional<Exercise> findByIdWithCategory(@Param("id") Long id);
}
