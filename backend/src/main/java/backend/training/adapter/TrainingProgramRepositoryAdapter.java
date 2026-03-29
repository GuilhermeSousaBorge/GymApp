package backend.training.adapter;

import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramCommandPort;
import backend.training.port.TrainingProgramQueryPort;
import backend.training.port.TrainingProgramValidationPort;
import backend.training.repository.TrainingProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR: TrainingProgramRepository → Ports
 *
 * Implementa as portas de TrainingProgram.
 * Padrão Adapter (Gang of Four)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TrainingProgramRepositoryAdapter 
        implements TrainingProgramQueryPort, TrainingProgramCommandPort, TrainingProgramValidationPort {
    
    private final TrainingProgramRepository trainingProgramRepository;
    
    // ========== TrainingProgramQueryPort Implementation ==========
    
    @Override
    public Optional<TrainingProgram> findById(Long id) {
        log.debug("TrainingProgramRepositoryAdapter: findById({})", id);
        return trainingProgramRepository.findById(id);
    }
    
    @Override
    public Optional<TrainingProgram> findByIdWithSheets(Long id) {
        log.debug("TrainingProgramRepositoryAdapter: findByIdWithSheets({})", id);
        return trainingProgramRepository.findByIdWithTrainingSheet(id);
    }
    
    @Override
    public Optional<TrainingProgram> findByName(String name) {
        log.debug("TrainingProgramRepositoryAdapter: findByName({})", name);
        return trainingProgramRepository.findByName(name);
    }
    
    @Override
    public List<TrainingProgram> findByStudentId(Long studentId) {
        log.debug("TrainingProgramRepositoryAdapter: findByStudentId({})", studentId);
        return trainingProgramRepository.findByStudentId(studentId);
    }
    
    @Override
    public Optional<TrainingProgram> findByTrainerId(Long trainerId) {
        log.debug("TrainingProgramRepositoryAdapter: findByTrainerId({})", trainerId);
        return trainingProgramRepository.findByTrainerId(trainerId);
    }
    
    @Override
    public int countActive() {
        log.debug("TrainingProgramRepositoryAdapter: countActive()");
        return trainingProgramRepository.countByActiveTrue();
    }
    
    @Override
    public List<TrainingProgram> findAll() {
        log.debug("TrainingProgramRepositoryAdapter: findAll()");
        return trainingProgramRepository.findAll();
    }
    
    // ========== TrainingProgramCommandPort Implementation ==========
    
    @Override
    public TrainingProgram save(TrainingProgram program) {
        log.debug("TrainingProgramRepositoryAdapter: save({})", program.getId());
        return trainingProgramRepository.save(program);
    }
    
    @Override
    public TrainingProgram update(TrainingProgram program) {
        log.debug("TrainingProgramRepositoryAdapter: update({})", program.getId());
        return trainingProgramRepository.save(program);
    }
    
    @Override
    public void deleteById(Long id) {
        log.debug("TrainingProgramRepositoryAdapter: deleteById({})", id);
        trainingProgramRepository.deleteById(id);
    }
    
    // ========== TrainingProgramValidationPort Implementation ==========
    
    @Override
    public boolean existsByNameAndIdDifferent(String name, Long id) {
        log.debug("TrainingProgramRepositoryAdapter: existsByNameAndIdDifferent({}, {})", name, id);
        return trainingProgramRepository.existsByNameAndId(name, id);
    }
    
    @Override
    public boolean existsByNameAndStudent(String name, Long studentId) {
        log.debug("TrainingProgramRepositoryAdapter: existsByNameAndStudent({}, {})", name, studentId);
        return trainingProgramRepository.existsByNameAndStudentId(name, studentId);
    }
}

