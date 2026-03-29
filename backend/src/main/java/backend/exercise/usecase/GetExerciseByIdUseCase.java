package backend.exercise.usecase;

import backend.exercise.dto.ExerciseResponse;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.port.ExerciseQueryPort;
import backend.infrastructure.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("exerciseGetExerciseByIdUseCase")
@Slf4j
@RequiredArgsConstructor
public class GetExerciseByIdUseCase {

    private final ExerciseQueryPort queryPort;
    private final ExerciseMapper mapper;

    @Transactional(readOnly = true)
    public ExerciseResponse execute(Long id) {
        log.info("Buscando exercício por ID: {}", id);

        // Usa query com JOIN FETCH para carregar categoria
        Exercise exercise = queryPort.findByIdWithCategory(id)
                .orElseThrow(() -> {
                    log.warn("Exercício não encontrado para ID: {}", id);
                    return new BadRequestException("Exercício não encontrado");
                });
        log.info("Exercício encontrado {}", id);
        return mapper.toResponse(exercise);
    }
}
