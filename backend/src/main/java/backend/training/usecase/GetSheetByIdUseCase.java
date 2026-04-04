package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSheetByIdUseCase {

    private final TrainingSheetQueryPort queryPort;
    private final TrainingSheetMapper mapper;

    @Transactional(readOnly = true)
    public TrainingSheetResponse execute(Long id){
        log.info("Buscando ficha de treino por ID: {}", id);

        TrainingSheet sheet = queryPort.findById(id).orElseThrow(() -> new BadRequestException("Ficha de treino não encontrada"));

        log.info("Ficha de treino com ID: {} encontrada", id);
        return mapper.toResponse(sheet);
    }
}
