package backend.training.adapter;

import backend.training.model.entity.TrainingSheet;
import backend.training.model.enums.DayOfWeek;
import backend.training.port.TrainingSheetCommandPort;
import backend.training.port.TrainingSheetQueryPort;
import backend.training.repository.TrainingSheetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR: TrainingSheetRepository → Ports
 *
 * Implementa as portas de TrainingSheet.
 * Padrão Adapter (Gang of Four)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TrainingSheetRepositoryAdapter implements TrainingSheetQueryPort, TrainingSheetCommandPort {
    
    private final TrainingSheetRepository trainingSheetRepository;
    
    // ========== TrainingSheetQueryPort Implementation ==========
    
    @Override
    public Optional<TrainingSheet> findById(Long id) {
        log.debug("TrainingSheetRepositoryAdapter: findById({})", id);
        return trainingSheetRepository.findById(id);
    }
    
    @Override
    public List<TrainingSheet> findByProgramId(Long programId) {
        log.debug("TrainingSheetRepositoryAdapter: findByProgramId({})", programId);
        return trainingSheetRepository.findByTrainingProgramId(programId);
    }
    
    @Override
    public Optional<TrainingSheet> findByIdWithExercises(Long id) {
        log.debug("TrainingSheetRepositoryAdapter: findByIdWithExercises({})", id);
        return trainingSheetRepository.findByIdWithProgram(id);
    }
    
    @Override
    public List<TrainingSheet> findAll() {
        log.debug("TrainingSheetRepositoryAdapter: findAll()");
        return trainingSheetRepository.findAll();
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("TrainingSheetRepositoryAdapter: existsByName({})", name);
        return trainingSheetRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndProgramId(String name, Long programId) {
        log.debug("TrainingSheetRepositoryAdapter: existsByNameAndProgramId({}, {})", name, programId);
        return trainingSheetRepository.existsByNameAndTrainingProgramId(name, programId);
    }

    @Override
    public int countByProgramId(Long programId) {
        log.debug("TrainingSheetRepositoryAdapter: countByProgramId({})", programId);
        return trainingSheetRepository.countByTrainingProgramId(programId);
    }

    @Override
    public boolean existsById(Long id) {
        log.debug("TrainingSheetRepositoryAdapter: existsById({})", id);
        return trainingSheetRepository.existsById(id);
    }

    @Override
    public List<TrainingSheet> findByProgramIdAndActive(Long programId) {
        log.debug("TrainingSheetRepositoryAdapter: findByProgramIdAndActive({})", programId);
        return trainingSheetRepository.findByTrainingProgramIdAndActiveTrue(programId);
    }

    @Override
    public List<TrainingSheet> findByDayOfWeek(DayOfWeek dayOfWeek) {
        log.debug("TrainingSheetRepositoryAdapter: findByDayOfWeek({})", dayOfWeek);
        return trainingSheetRepository.findByWeekdaysContaining(dayOfWeek);
    }
    
    // ========== TrainingSheetCommandPort Implementation ==========
    
    @Override
    public TrainingSheet save(TrainingSheet sheet) {
        log.debug("TrainingSheetRepositoryAdapter: save({})", sheet.getId());
        return trainingSheetRepository.save(sheet);
    }
    
    @Override
    public TrainingSheet update(TrainingSheet sheet) {
        log.debug("TrainingSheetRepositoryAdapter: update({})", sheet.getId());
        return trainingSheetRepository.save(sheet);
    }
    
    @Override
    public void deleteById(Long id) {
        log.debug("TrainingSheetRepositoryAdapter: deleteById({})", id);
        trainingSheetRepository.deleteById(id);
    }
}

