package backend.training.adapter;

import backend.training.model.entity.TrainingExercise;
import backend.training.port.TrainingExerciseCommandPort;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.repository.TrainingExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR: TrainingExerciseRepository → Ports
 *
 * Implementa as portas de TrainingExercise.
 * Padrão Adapter (Gang of Four)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TrainingExerciseRepositoryAdapter implements TrainingExerciseQueryPort, TrainingExerciseCommandPort {
    
    private final TrainingExerciseRepository trainingExerciseRepository;
    
    // ========== TrainingExerciseQueryPort Implementation ==========
    
    @Override
    public Optional<TrainingExercise> findById(Long id) {
        log.debug("TrainingExerciseRepositoryAdapter: findById({})", id);
        return trainingExerciseRepository.findById(id);
    }
    
    @Override
    public List<TrainingExercise> findBySheetId(Long sheetId) {
        log.debug("TrainingExerciseRepositoryAdapter: findBySheetId({})", sheetId);
        return trainingExerciseRepository.findByTrainingSheetId(sheetId);
    }
    
    @Override
    public Optional<TrainingExercise> findByIdWithExerciseDetails(Long id) {
        log.debug("TrainingExerciseRepositoryAdapter: findByIdWithExerciseDetails({})", id);
        return trainingExerciseRepository.findByIdWithExercise(id);
    }
    
    @Override
    public List<TrainingExercise> findAll() {
        log.debug("TrainingExerciseRepositoryAdapter: findAll()");
        return trainingExerciseRepository.findAll();
    }

    @Override
    public List<TrainingExercise> findBySheetWithExercise(Long sheetId) {
        log.debug("TrainingExerciseRepositoryAdapter: findBySheetWithExercise({})", sheetId);
        return trainingExerciseRepository.findBySheetWithExercise(sheetId);
    }

    @Override
    public boolean existsBySheetAndExercise(Long sheetId, Long exerciseId) {
        log.debug("TrainingExerciseRepositoryAdapter: existsBySheetAndExercise({}, {})", sheetId, exerciseId);
        return trainingExerciseRepository.existsByTrainingSheetIdAndExerciseId(sheetId, exerciseId);
    }

    @Override
    public int countBySheet(Long sheetId) {
        log.debug("TrainingExerciseRepositoryAdapter: countBySheet({})", sheetId);
        return trainingExerciseRepository.countByTrainingSheetId(sheetId);
    }

    @Override
    public boolean existsBySheet(Long sheetId) {
        log.debug("TrainingExerciseRepositoryAdapter: existsBySheet({})", sheetId);
        return trainingExerciseRepository.existsByTrainingSheetId(sheetId);
    }
    
    // ========== TrainingExerciseCommandPort Implementation ==========
    
    @Override
    public TrainingExercise save(TrainingExercise exercise) {
        log.debug("TrainingExerciseRepositoryAdapter: save({})", exercise.getId());
        return trainingExerciseRepository.save(exercise);
    }
    
    @Override
    public TrainingExercise update(TrainingExercise exercise) {
        log.debug("TrainingExerciseRepositoryAdapter: update({})", exercise.getId());
        return trainingExerciseRepository.save(exercise);
    }
    
    @Override
    public void deleteById(Long id) {
        log.debug("TrainingExerciseRepositoryAdapter: deleteById({})", id);
        trainingExerciseRepository.deleteById(id);
    }
}

