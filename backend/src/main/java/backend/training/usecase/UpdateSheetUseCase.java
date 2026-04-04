package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
import backend.training.dto.TrainingSheetUpdateRequest;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingSheetCommandPort;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateSheetUseCase {

    private final TrainingSheetQueryPort queryPort;
    private final TrainingSheetCommandPort commandPort;
    private final TrainingSheetMapper mapper;

    @Transactional
    public TrainingSheetResponse execute(Long id, TrainingSheetUpdateRequest request){
        log.info("Atualizando folha ID: {}", id);

        TrainingSheet sheet = queryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ficha de treino não encontrada para ID: {}", id);
                    return new BadRequestException("Ficha de treino não encontrada para ID: " + id);
                });

        // Validar nome duplicado (se mudou)
        if (request.getName() != null
                && !request.getName().equals(sheet.getName())
                && queryPort.existsByNameAndProgramId(
                request.getName(), sheet.getTrainingProgram().getId())) {
            throw new BadRequestException("Já existe uma folha com este nome neste programa");
        }

        // Validar weekdays (se mudou)
        if (request.getWeekdays() != null) {
            if (request.getWeekdays().isEmpty()) {
                throw new BadRequestException("Folha deve ter pelo menos 1 dia de treino");
            }
            if (request.getWeekdays().size() > 7) {
                throw new BadRequestException("Não pode ter mais de 7 dias na semana");
            }
        }

        // Atualizar campos
        sheet.updateFrom(request);

        TrainingSheet updated = commandPort.update(sheet);

        log.info("Folha atualizada: {}", updated.getName());

        return mapper.toResponse(updated);
    }
}
