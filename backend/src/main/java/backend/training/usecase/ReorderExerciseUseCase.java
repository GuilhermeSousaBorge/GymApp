package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.mapper.TrainingExerciseMapper;
import backend.training.model.entity.TrainingExercise;
import backend.training.port.TrainingExerciseCommandPort;
import backend.training.port.TrainingExerciseQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReorderExerciseUseCase {

    private final TrainingExerciseQueryPort queryPort;
    private final TrainingExerciseCommandPort commandPort;
    private final TrainingExerciseMapper mapper;

    @Transactional
    public TrainingExerciseResponse execute(Long exerciseId, Integer newOrder) {
        log.info("Reordenando exercício {} para ordem {}", exerciseId, newOrder);

        if (newOrder == null || newOrder < 1) {
            throw new BadRequestException("Ordem deve ser maior que 0");
        }

        TrainingExercise trainingExercise = queryPort.findById(exerciseId)
                .orElseThrow(() -> new BadRequestException("Exercício de treinamento não encontrado"));

        trainingExercise.setOrderInSheet(newOrder);

        TrainingExercise updatedExercise = commandPort.update(trainingExercise);

        log.info("Exercício de treinamento {} reordenado para {}", updatedExercise.getId(), newOrder);

        return mapper.toResponse(updatedExercise);
    }
}

