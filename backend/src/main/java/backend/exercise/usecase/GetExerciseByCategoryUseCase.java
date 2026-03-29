package backend.exercise.usecase;

import backend.exercise.dto.ExerciseResponse;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.exercise.port.ExerciseQueryPort;
import backend.infrastructure.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetExerciseByCategoryUseCase {

    private final ExerciseQueryPort queryPort;
    private final ExerciseCategoryQueryPort categoryQueryPort;
    private final ExerciseMapper mapper;

    @Transactional(readOnly = true)
    public List<ExerciseResponse> execute(Long categoryId){
        log.info("Buscando exercícios da categoria ID: {}", categoryId);

        // Validar se categoria existe
        if (categoryQueryPort.findById(categoryId).isEmpty()) {
            throw new BadRequestException("Categoria não encontrada");
        }

        return queryPort.findByCategoryActive(categoryId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
