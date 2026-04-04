package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.port.TrainingSheetCommandPort;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeleteSheetUseCase {

    private final TrainingSheetQueryPort sheetQueryPort;
    private final TrainingSheetCommandPort sheetCommandPort;
    private final TrainingExerciseQueryPort exerciseQueryPort;

    @Transactional
    public void execute(Long id){
        log.info("Deletando permanentemente folha ID: {}", id);

        TrainingSheet sheet = sheetQueryPort.findById(id).orElseThrow(() -> new BadRequestException("Ficha nao encontrada"));

        // TODO: Verificar se tem exercícios associados
         if (exerciseQueryPort.existsBySheet(id)) {
             throw new BadRequestException("Não é possível deletar folha com exercícios");
         }

        sheetCommandPort.deleteById(sheet.getId());

        log.warn("Folha deletada: {}", sheet.getName());
    }
}
