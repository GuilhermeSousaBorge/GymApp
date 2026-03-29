package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingSheetCommandPort;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeactivateSheetUseCase {

    private final TrainingSheetQueryPort queryPort;
    private final TrainingSheetCommandPort commandPort;

    @Transactional
    public void execute(Long id){
        log.info("Desativando folha ID: {}", id);

        TrainingSheet sheet = queryPort.findById(id).orElseThrow(() -> {
            log.warn("Ficha de treino não encontrada para ID: {}", id);
            return new BadRequestException("Ficha de treino não encontrada para ID: " + id);
        });

        if (!sheet.getActive()) {
            throw new BadRequestException("Folha já está inativa");
        }

        sheet.setActive(false);

        commandPort.update(sheet);

        log.info("Folha desativada: {}", sheet.getName());
    }
}
