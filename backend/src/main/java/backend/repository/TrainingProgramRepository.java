package backend.repository;

import backend.model.entity.TrainingProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {

    Optional<TrainingProgram> findByName(String name);

    List<TrainingProgram> findByStudentId(Long id);

    Optional<TrainingProgram> findByTrainerId(Long id);

    boolean existsByNameAndId(String name, Long id);

    boolean existsByNameAndStudentId(String name, Long userId);

    @Query("""
        SELECT tp FROM TrainingProgram tp
        LEFT JOIN FETCH tp.trainingSheets ts
        WHERE tp.id = :id
    """)
    Optional<TrainingProgram> findByIdWithTrainingSheet(@Param("id") Long id);

    int countByActiveTrue();

}
