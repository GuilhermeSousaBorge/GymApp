package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingProgramResponse;
import backend.training.mapper.TrainingProgramMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetProgramByIdUseCase {

    private final TrainingProgramQueryPort queryPort;
    private final TrainingProgramMapper mapper;

    @Transactional(readOnly = true)
    public TrainingProgramResponse execute(Long id){
        log.info("Buscando programa de treinamento por ID: {}", id);

        TrainingProgram trainingProgram = queryPort.findByIdWithSheets(id)
                .orElseThrow(() -> {
                    log.warn("Programa de treinamento não encontrado: ID {}", id);
                    return new BadRequestException("Programa de treinamento não encontrado");
                });

        return mapper.toResponse(trainingProgram);
    }
}
