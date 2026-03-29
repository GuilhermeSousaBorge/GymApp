package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.port.TrainingProgramQueryPort;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetActiveSheetsFromProgramUseCase {

    private final TrainingSheetQueryPort sheetQueryPort;
    private final TrainingProgramQueryPort programQueryPort;
    private final TrainingSheetMapper mapper;

    @Transactional(readOnly = true)
    public List<TrainingSheetResponse> execute(Long programId){
        log.info("Buscando folhas ativas do programa ID: {}", programId);

        if (programQueryPort.findById(programId).isEmpty()) {
            throw new BadRequestException("Programa não encontrado");
        }

        return sheetQueryPort.findByProgramIdAndActive(programId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
