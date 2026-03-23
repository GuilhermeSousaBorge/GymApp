package backend.training.service;

import backend.training.dto.TrainingExerciseRequest;
import backend.training.dto.TrainingExerciseUpdateRequest;
import backend.training.dto.TrainingExerciseResponse;
import backend.infrastructure.exception.BadRequestException;
import backend.training.mapper.TrainingExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.training.model.entity.TrainingExercise;
import backend.training.model.entity.TrainingSheet;
import backend.exercise.repository.ExerciseRepository;
import backend.training.repository.TrainingExerciseRepository;
import backend.training.repository.TrainingSheetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingExerciseService {

    private final TrainingExerciseRepository trainingExerciseRepository;
    private final TrainingSheetRepository trainingSheetRepository;
    private final ExerciseRepository exerciseRepository;
    private final TrainingExerciseMapper trainingExerciseMapper;

    @Transactional
    public TrainingExerciseResponse createTrainingExercise(TrainingExerciseRequest trainingExercise) {
        log.info("Criando exercício de treinamento: {}", trainingExercise.getExerciseId());

        TrainingSheet trainingSheet = trainingSheetRepository.findById(trainingExercise.getTrainingSheetId())
                .orElseThrow(() -> new BadRequestException("Ficha de treinamento não encontrada"));
        if(!trainingSheet.getActive()) {
            throw new BadRequestException("Ficha de treinamento inativa");
        }

        Exercise exercise = exerciseRepository.findById(trainingExercise.getExerciseId())
                .orElseThrow(() -> new BadRequestException("Exercício não encontrado"));

        if(!exercise.getActive()) {
            throw new BadRequestException("Exercício inativo");
        }

        if(trainingExerciseRepository.existsByTrainingSheetIdAndExerciseId(trainingExercise.getTrainingSheetId(), trainingExercise.getExerciseId())) {
            throw new BadRequestException("Exercício já adicionado à ficha de treinamento");
        }

        TrainingExercise newTrainingExercise = TrainingExercise.builder()
                .trainingSheet(trainingSheet)
                .exercise(exercise)
                .sets(trainingExercise.getSets())
                .reps(trainingExercise.getReps())
                .orderInSheet(trainingExerciseRepository.countByTrainingSheetId(trainingExercise.getTrainingSheetId()) + 1)
                .build();

        trainingExerciseRepository.save(newTrainingExercise);

        return trainingExerciseMapper.toResponse(newTrainingExercise);
    }

    @Transactional(readOnly = true)
    public TrainingExerciseResponse getTrainingExerciseById(long id) {
        log.info("Buscando exercício de treinamento por ID: {}", id);
        TrainingExercise trainingExercise = trainingExerciseRepository.findByIdWithExercise(id)
                .orElseThrow(() -> new BadRequestException("Exercício de treinamento não encontrado"));
        return trainingExerciseMapper.toResponse(trainingExercise);
    }

    public List<TrainingExerciseResponse> getAllTrainingExercises() {
        log.info("Buscando todos os exercícios de treinamento");
        return trainingExerciseRepository.findAll().stream()
                .map(trainingExerciseMapper::toResponse)
                .toList();
    }

    @Transactional
    public TrainingExerciseResponse updateTrainingExercise(long id, TrainingExerciseUpdateRequest request) {
        log.info("Atualizando exercício de treinamento por ID: {}", id);
        TrainingExercise trainingExercise = trainingExerciseRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Exercício de treinamento não encontrado"));

        trainingExercise.updateFrom(request);


        TrainingExercise updatedTrainingExercise = trainingExerciseRepository.save(trainingExercise);
        log.info("Exercício de treinamento atualizado com sucesso: ID {}", id);
        return trainingExerciseMapper.toResponse(updatedTrainingExercise);
    }

    @Transactional
    public void deleteTrainingExercise(long id) {
        log.info("Deletando exercício de treinamento por ID: {}", id);
        TrainingExercise trainingExercise = trainingExerciseRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Exercício de treinamento não encontrado"));
        trainingExerciseRepository.delete(trainingExercise);
        log.info("Exercício de treinamento deletado com sucesso: ID {}", id);
    }

    /**
     * Buscar exercícios de uma folha
     */
    @Transactional(readOnly = true)
    public List<TrainingExerciseResponse> getExercisesFromSheet(Long sheetId) {
        log.info("Buscando exercícios da folha ID: {}", sheetId);

        if (!trainingSheetRepository.existsById(sheetId)) {
            throw new BadRequestException("Folha não encontrada");
        }

        return trainingExerciseRepository.findBySheetWithExercise(sheetId)
                .stream()
                .map(trainingExerciseMapper::toResponse)
                .toList();
    }

    /**
     * Reordenar exercício
     */
    @Transactional
    public TrainingExerciseResponse reorderExercise(Long id, Integer newOrder) {
        log.info("Reordenando exercício {} para ordem {}", id, newOrder);

        TrainingExercise trainingExercise = findTrainingExerciseById(id);

        if (newOrder == null || newOrder < 1) {
            throw new BadRequestException("Ordem deve ser maior que 0");
        }

        trainingExercise.setOrderInSheet(newOrder);

        TrainingExercise updated = trainingExerciseRepository.save(trainingExercise);

        return trainingExerciseMapper.toResponse(updated);
    }

    private TrainingExercise findTrainingExerciseById(long id) {

        return trainingExerciseRepository.findById(id).orElseThrow(() -> {
            log.error("Exercício não encontrado: ID {}", id);
            return new RuntimeException("Exercício de treinamento não encontrado");
        });
    }
}
