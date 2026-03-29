package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.mapper.TrainingExerciseMapper;
import backend.training.model.entity.TrainingExercise;
import backend.training.port.TrainingExerciseQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("trainingGetExerciseByIdUseCase")
@Slf4j
@RequiredArgsConstructor
public class GetExerciseByIdUseCase {

    private final TrainingExerciseQueryPort queryPort;
    private final TrainingExerciseMapper mapper;

    @Transactional(readOnly = true)
    public TrainingExerciseResponse execute(Long id){
        log.info("Buscando exercício de treinamento por ID: {}", id);
        TrainingExercise trainingExercise = queryPort.findByIdWithExerciseDetails(id)
                .orElseThrow(() -> new BadRequestException("Exercício de treinamento não encontrado"));
        return mapper.toResponse(trainingExercise);
    }
}
