package backend.training.repository;

import backend.training.model.entity.TrainingSheet;
import backend.training.model.enums.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingSheetRepository extends JpaRepository<TrainingSheet, Long> {

    Optional<TrainingSheet> findByName(String name);

    List<TrainingSheet> findByActiveTrue();

    boolean existsByName(String name);


    // Buscar por programa
    List<TrainingSheet> findByTrainingProgramId(Long programId);

    // Buscar folhas ativas de um programa
    List<TrainingSheet> findByTrainingProgramIdAndActiveTrue(Long programId);

    // Verificar se nome existe no programa
    boolean existsByNameAndTrainingProgramId(String name, Long programId);

    // Buscar por dia da semana
    @Query("""
        SELECT ts FROM TrainingSheet ts
        WHERE :dayOfWeek MEMBER OF ts.weekdays
    """)
    List<TrainingSheet> findByWeekdaysContaining(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    // Buscar folhas ativas

    // Buscar com programa carregado (JOIN FETCH)
    @Query("""
        SELECT ts FROM TrainingSheet ts
        JOIN FETCH ts.trainingProgram
        WHERE ts.id = :id
    """)
    Optional<TrainingSheet> findByIdWithProgram(@Param("id") Long id);

    int countByTrainingProgramId(Long trainingProgramId);
}
