package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingProgramRequest;
import backend.training.dto.TrainingProgramResponse;
import backend.training.mapper.TrainingProgramMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramCommandPort;
import backend.training.port.TrainingProgramValidationPort;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateProgramUseCase {

    private final TrainingProgramCommandPort commandPort;
    private final TrainingProgramValidationPort validationPort;
    private final UserQueryPort userQueryPort;
    private final TrainingProgramMapper mapper;

    @Transactional
    public TrainingProgramResponse execute(TrainingProgramRequest request){
        log.info("Criando programa de treinamento: {}", request.getName());

        if (validationPort.existsByNameAndStudent(request.getName(), request.getUserId())) {
            throw new BadRequestException("Já existe um programa de treinamento com este nome para este aluno");
        }

        User student = userQueryPort.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("Aluno não encontrado ou inativo"));

        User trainer = null;
        if (request.getTrainerId() != null) {
            trainer = userQueryPort.findById(request.getTrainerId())
                    .orElseThrow(() -> new BadRequestException("Personal não encontrado ou inativo"));
        }

        TrainingProgram trainingProgram = TrainingProgram.builder().
                name(request.getName()).
                description(request.getDescription()).
                student(student).
                trainer(trainer).
                active(true).
                build();

        TrainingProgram trainingProgramSaved = commandPort.save(trainingProgram);
        log.info("Programa de treinamento criado com sucesso: {} (ID {})", trainingProgramSaved.getName(), trainingProgramSaved.getId());

        return mapper.toResponse(trainingProgramSaved);
    }
}
