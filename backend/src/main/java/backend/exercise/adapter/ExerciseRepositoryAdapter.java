package backend.exercise.adapter;

import backend.exercise.model.entity.Exercise;
import backend.exercise.port.ExerciseCommandPort;
import backend.exercise.port.ExerciseQueryPort;
import backend.exercise.port.ExerciseValidationPort;
import backend.exercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR: ExerciseRepository → Ports
 *
 * Implementa as portas de Exercise.
 * Padrão Adapter (Gang of Four)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExerciseRepositoryAdapter 
        implements ExerciseQueryPort, ExerciseCommandPort, ExerciseValidationPort {
    
    private final ExerciseRepository exerciseRepository;
    
    // ========== ExerciseQueryPort Implementation ==========
    
    @Override
    public Optional<Exercise> findById(Long id) {
        log.debug("ExerciseRepositoryAdapter: findById({})", id);
        return exerciseRepository.findById(id);
    }
    
    @Override
    public Optional<Exercise> findByIdWithCategory(Long id) {
        log.debug("ExerciseRepositoryAdapter: findByIdWithCategory({})", id);
        return exerciseRepository.findByIdWithCategory(id);
    }
    
    @Override
    public List<Exercise> findAllActive() {
        log.debug("ExerciseRepositoryAdapter: findAllActive()");
        return exerciseRepository.findByActiveTrue();
    }
    
    @Override
    public List<Exercise> findByCategory(Long categoryId) {
        log.debug("ExerciseRepositoryAdapter: findByCategory({})", categoryId);
        return exerciseRepository.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Exercise> findByCategoryActive(Long categoryId) {
        log.debug("ExerciseRepositoryAdapter: findByCategoryActive({})", categoryId);
        return exerciseRepository.findByCategoryIdAndActiveTrue(categoryId);
    }
    
    @Override
    public List<Exercise> search(String searchTerm) {
        log.debug("ExerciseRepositoryAdapter: search({})", searchTerm);
        return exerciseRepository.searchExercises(searchTerm);
    }
    
    @Override
    public List<Exercise> findAll() {
        log.debug("ExerciseRepositoryAdapter: findAll()");
        return exerciseRepository.findAll();
    }
    
    // ========== ExerciseCommandPort Implementation ==========
    
    @Override
    public Exercise save(Exercise exercise) {
        log.debug("ExerciseRepositoryAdapter: save({})", exercise.getId());
        return exerciseRepository.save(exercise);
    }
    
    @Override
    public Exercise update(Exercise exercise) {
        log.debug("ExerciseRepositoryAdapter: update({})", exercise.getId());
        return exerciseRepository.save(exercise);
    }
    
    @Override
    public void deleteById(Long id) {
        log.debug("ExerciseRepositoryAdapter: deleteById({})", id);
        exerciseRepository.deleteById(id);
    }
    
    // ========== ExerciseValidationPort Implementation ==========
    
    @Override
    public boolean existsByName(String name) {
        log.debug("ExerciseRepositoryAdapter: existsByName({})", name);
        return exerciseRepository.existsByName(name);
    }
}

