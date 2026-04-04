package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
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
public class ReorderSheetUseCase {

    private final TrainingSheetQueryPort queryPort;
    private final TrainingSheetCommandPort commandPort;
    private final TrainingSheetMapper mapper;

    @Transactional
    public TrainingSheetResponse execute(Long sheetId, Integer newOrder) {
        log.info("Alterando ordem da folha {} para {}", sheetId, newOrder);

        if (newOrder == null || newOrder < 1) {
            throw new BadRequestException("Ordem deve ser maior que 0");
        }

        TrainingSheet sheet = queryPort.findById(sheetId)
                .orElseThrow(() -> new BadRequestException("Ficha de treino não encontrada"));

        sheet.setOrderInProgram(newOrder);

        TrainingSheet updatedSheet = commandPort.update(sheet);

        log.info("Ordem da folha {} alterada para {}", updatedSheet.getName(), newOrder);

        return mapper.toResponse(updatedSheet);
    }
}

