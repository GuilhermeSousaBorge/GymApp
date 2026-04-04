package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramCommandPort;
import backend.training.port.TrainingProgramQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteProgramUseCase {

    private final TrainingProgramQueryPort queryPort;
    private final TrainingProgramCommandPort commandPort;

    @Transactional
    public void execute(Long id){
        log.info("Deletando permanentemente programa de treinamento ID: {}", id);

        TrainingProgram trainingProgram = queryPort.findById(id).orElseThrow(() -> new BadRequestException("Ficha nao encontrada"));

        if (trainingProgram.getTrainingSheets() != null && !trainingProgram.getTrainingSheets().isEmpty()) {
            throw new BadRequestException("Não é possível deletar programa com folhas associadas");
        }

        commandPort.deleteById(trainingProgram.getId());
    }
}
