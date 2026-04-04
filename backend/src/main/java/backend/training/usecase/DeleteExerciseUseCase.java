package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.model.entity.TrainingExercise;
import backend.training.port.TrainingExerciseCommandPort;
import backend.training.port.TrainingExerciseQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("trainingDeleteExerciseUseCase")
@Slf4j
@RequiredArgsConstructor
public class DeleteExerciseUseCase {

    private final TrainingExerciseQueryPort queryPort;
    private final TrainingExerciseCommandPort commandPort;

    @Transactional
    public void execute(Long id){
        log.info("Deletando exercício de treinamento por ID: {}", id);
        TrainingExercise trainingExercise = queryPort.findById(id)
                .orElseThrow(() -> new BadRequestException("Exercício de treinamento não encontrado"));
        commandPort.deleteById(trainingExercise.getId());
        log.info("Exercício de treinamento deletado com sucesso: ID {}", id);
    }
}
