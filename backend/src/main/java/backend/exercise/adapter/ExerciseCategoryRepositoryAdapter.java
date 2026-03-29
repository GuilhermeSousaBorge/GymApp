package backend.exercise.adapter;

import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryCommandPort;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.exercise.repository.ExerciseCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR: ExerciseCategoryRepository → Ports
 *
 * Implementa as portas de ExerciseCategory.
 * Padrão Adapter (Gang of Four)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExerciseCategoryRepositoryAdapter 
        implements ExerciseCategoryQueryPort, ExerciseCategoryCommandPort {
    
    private final ExerciseCategoryRepository categoryRepository;
    
    // ========== ExerciseCategoryQueryPort Implementation ==========
    
    @Override
    public Optional<ExerciseCategory> findById(Long id) {
        log.debug("ExerciseCategoryRepositoryAdapter: findById({})", id);
        return categoryRepository.findById(id);
    }
    
    @Override
    public Optional<ExerciseCategory> findByName(String name) {
        log.debug("ExerciseCategoryRepositoryAdapter: findByName({})", name);
        return categoryRepository.findByMuscleGroup(name);
    }
    
    @Override
    public List<ExerciseCategory> findAllActive() {
        log.debug("ExerciseCategoryRepositoryAdapter: findAllActive()");
        return categoryRepository.findByActiveTrue();
    }
    
    @Override
    public List<ExerciseCategory> findAll() {
        log.debug("ExerciseCategoryRepositoryAdapter: findAll()");
        return categoryRepository.findAll();
    }
    
    // ========== ExerciseCategoryCommandPort Implementation ==========
    
    @Override
    public ExerciseCategory save(ExerciseCategory category) {
        log.debug("ExerciseCategoryRepositoryAdapter: save({})", category.getId());
        return categoryRepository.save(category);
    }
    
    @Override
    public ExerciseCategory update(ExerciseCategory category) {
        log.debug("ExerciseCategoryRepositoryAdapter: update({})", category.getId());
        return categoryRepository.save(category);
    }
    
    @Override
    public void deleteById(Long id) {
        log.debug("ExerciseCategoryRepositoryAdapter: deleteById({})", id);
        categoryRepository.deleteById(id);
    }
}

