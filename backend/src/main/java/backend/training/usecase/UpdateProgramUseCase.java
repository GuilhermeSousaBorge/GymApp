package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingProgramResponse;
import backend.training.dto.TrainingProgramUpdateRequest;
import backend.training.mapper.TrainingProgramMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramCommandPort;
import backend.training.port.TrainingProgramQueryPort;
import backend.training.port.TrainingProgramValidationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateProgramUseCase {

    private final TrainingProgramQueryPort queryPort;
    private final TrainingProgramValidationPort validationPort;
    private final TrainingProgramCommandPort commandPort;
    private final TrainingProgramMapper mapper;

    @Transactional
    public TrainingProgramResponse execute(Long id, TrainingProgramUpdateRequest request){
        log.info("Atualizando programa de treinamento: ID {}", id);

        TrainingProgram trainingProgram = queryPort.findById(id).orElseThrow(() -> new BadRequestException("Programa nao encontrado"));

        if (request.getName() != null
                && !request.getName().equals(trainingProgram.getName())
                && validationPort.existsByNameAndStudent(request.getName(), trainingProgram.getStudent().getId())) {
            throw new BadRequestException("Já existe um programa com este nome para este aluno");
        }

        trainingProgram.updateFrom(request);

        TrainingProgram updated = commandPort.update(trainingProgram);
        log.info("Atualizando programa de treinamento: ID {}", id);

        return mapper.toResponse(updated);
    }
}
