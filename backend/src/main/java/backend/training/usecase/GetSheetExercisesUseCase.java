package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.mapper.TrainingExerciseMapper;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSheetExercisesUseCase {

    private final TrainingExerciseQueryPort exerciseQueryPort;
    private final TrainingSheetQueryPort sheetQueryPort;
    private final TrainingExerciseMapper mapper;

    @Transactional(readOnly = true)
    public List<TrainingExerciseResponse> execute(Long sheetId){
        log.info("Buscando exercícios da folha ID: {}", sheetId);

        if (!sheetQueryPort.existsById(sheetId)) {
            throw new BadRequestException("Folha não encontrada");
        }

        return exerciseQueryPort.findBySheetWithExercise(sheetId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
