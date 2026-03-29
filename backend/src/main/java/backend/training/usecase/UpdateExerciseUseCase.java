package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.dto.TrainingExerciseUpdateRequest;
import backend.training.mapper.TrainingExerciseMapper;
import backend.training.model.entity.TrainingExercise;
import backend.training.port.TrainingExerciseCommandPort;
import backend.training.port.TrainingExerciseQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("trainingUpdateExerciseUseCase")
@Slf4j
@RequiredArgsConstructor
public class UpdateExerciseUseCase {

    private final TrainingExerciseQueryPort queryPort;
    private final TrainingExerciseCommandPort commandPort;
    private final TrainingExerciseMapper mapper;

    @Transactional
    public TrainingExerciseResponse execute(Long id, TrainingExerciseUpdateRequest request){
        log.info("Atualizando exercício de treinamento por ID: {}", id);
        TrainingExercise trainingExercise = queryPort.findById(id)
                .orElseThrow(() -> new BadRequestException("Exercício de treinamento não encontrado"));

        trainingExercise.updateFrom(request);


        TrainingExercise updatedTrainingExercise = commandPort.update(trainingExercise);
        log.info("Exercício de treinamento atualizado com sucesso: ID {}", id);
        return mapper.toResponse(updatedTrainingExercise);
    }
}
