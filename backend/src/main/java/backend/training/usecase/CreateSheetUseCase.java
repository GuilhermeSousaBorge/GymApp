package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetRequest;
import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingProgramQueryPort;
import backend.training.port.TrainingSheetCommandPort;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateSheetUseCase {

    private final TrainingSheetQueryPort sheetQueryPort;
    private final TrainingSheetCommandPort sheetCommandPort;
    private final TrainingProgramQueryPort programQueryPort;
    private final TrainingSheetMapper mapper;

    @Transactional
    public TrainingSheetResponse execute(TrainingSheetRequest request){
        log.info("Criando ficha de treino: {}", request.getName());
        if (sheetQueryPort.existsByName(request.getName())) {
            throw new BadRequestException("Já existe uma ficha de treino com este nome");
        }

        TrainingProgram program = programQueryPort.findById(request.getTrainingProgramId())
                .orElseThrow(() -> new BadRequestException("Programa de treino não encontrado"));

        if(!program.getActive()) {
            throw new BadRequestException("Não é possível criar ficha de treino para programa inativo");
        }

        if(request.getWeekDays() == null || request.getWeekDays().isEmpty()) {
            throw new BadRequestException("A ficha de treino deve ter pelo menos um dia da semana definido");
        }

        if(request.getWeekDays().size() > 7) {
            throw new BadRequestException("A ficha de treino não pode ter mais de 7 dias da semana");
        }

        TrainingSheet sheet = TrainingSheet.builder()
                .name(request.getName())
                .description(request.getDescription())
                .trainingProgram(program)
                .orderInProgram(sheetQueryPort.countByProgramId(program.getId()) + 1)
                .weekdays(request.getWeekDays())
                .active(true)
                .build();

        TrainingSheet savedSheet = sheetCommandPort.save(sheet);
        log.info("Ficha de treino criada com sucesso: {} (ID {})", savedSheet.getName(), savedSheet.getId());
        return mapper.toResponse(savedSheet);
    }
}
