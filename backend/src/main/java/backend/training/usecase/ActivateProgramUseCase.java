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
public class ActivateProgramUseCase {

    private final TrainingProgramQueryPort queryPort;
    private final TrainingProgramCommandPort commandPort;

    @Transactional
    public void execute(Long id){
        log.info("Ativando programa ID: {}", id);

        TrainingProgram trainingProgram = queryPort.findById(id).orElseThrow(() -> new BadRequestException("Programa nao encontrado"));

        if (trainingProgram.getActive()) {
            throw new BadRequestException("Programa já está ativo");
        }

        trainingProgram.setActive(true);

        commandPort.update(trainingProgram);

        log.info("Folha programa: {}", trainingProgram.getName());

    }
}
