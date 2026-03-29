package backend.exercise.usecase;

import backend.exercise.dto.ExerciseCategoryRequest;
import backend.exercise.dto.ExerciseCategoryResponse;
import backend.exercise.mapper.ExerciseCategoryMapper;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryCommandPort;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.infrastructure.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateCategoryUseCase {

    private final ExerciseCategoryQueryPort queryPort;
    private final ExerciseCategoryCommandPort commandPort;
    private final ExerciseCategoryMapper mapper;

    @Transactional
    public ExerciseCategoryResponse execute(ExerciseCategoryRequest request){
        log.info("Criando categoria: {}", request.getMuscleGroup());

        // Validar duplicação
        if (queryPort.findByName(request.getMuscleGroup()).isPresent()) {
            throw new BadRequestException("Categoria com este nome já existe");
        }

        ExerciseCategory category = ExerciseCategory.builder()
                .muscleGroup(request.getMuscleGroup())
                .description(request.getDescription())
                .active(true)
                .build();

        ExerciseCategory savedCategory = commandPort.save(category);

        log.info("Categoria criada com sucesso: {} (ID {})",
                savedCategory.getMuscleGroup(), savedCategory.getId());
        log.info("Categoria criada: {}", request.getMuscleGroup());
        return mapper.toResponse(savedCategory);
    }
}
