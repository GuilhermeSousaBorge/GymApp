package backend.training.repository;

import backend.training.model.entity.TrainingExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingExerciseRepository extends JpaRepository<TrainingExercise, Long> {

    @Query("""
        SELECT te FROM TrainingExercise te
        JOIN FETCH te.exercise e
        WHERE te.id = :id
    """)
    Optional<TrainingExercise> findByIdWithExercise(@Param("id") Long id);

    // ⭐ Buscar exercícios de uma folha
    List<TrainingExercise> findByTrainingSheetId(Long sheetId);


    // ⭐ Buscar exercícios de uma folha com exercise carregado
    @Query("""
        SELECT te FROM TrainingExercise te
        JOIN FETCH te.exercise e
        JOIN FETCH e.category
        WHERE te.trainingSheet.id = :sheetId
        ORDER BY te.orderInSheet ASC
    """)
    List<TrainingExercise> findBySheetWithExercise(@Param("sheetId") Long sheetId);

    @Query("""
        SELECT te FROM TrainingExercise te
        JOIN FETCH te.trainingSheet ts
        JOIN FETCH te.exercise e
        JOIN FETCH e.category
        WHERE ts.trainingProgram.id = :programId
        ORDER BY ts.orderInProgram ASC, te.orderInSheet ASC
    """)
    List<TrainingExercise> findByProgramWithExercise(@Param("programId") Long programId);

    // ⭐ Verificar se exercício já está na folha
    boolean existsByTrainingSheetIdAndExerciseId(Long sheetId, Long exerciseId);

    boolean existsByExerciseId(Long exerciseId);

    int countByTrainingSheetId(Long trainingSheetId);

    boolean existsByTrainingSheetId(Long sheetId);
}
