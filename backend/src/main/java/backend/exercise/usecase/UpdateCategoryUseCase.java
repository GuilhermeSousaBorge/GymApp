package backend.exercise.usecase;

import backend.exercise.dto.ExerciseCategoryResponse;
import backend.exercise.dto.ExerciseCategoryUpdateRequest;
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
public class UpdateCategoryUseCase {

    private final ExerciseCategoryQueryPort queryPort;
    private final ExerciseCategoryCommandPort commandPort;
    private final ExerciseCategoryMapper mapper;

    @Transactional
    public ExerciseCategoryResponse execute(Long id, ExerciseCategoryUpdateRequest request){
        log.info("Atualizando categoria com ID: {}", id);

        ExerciseCategory category = queryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada para ID: {}", id);
                    return new BadRequestException("Categoria não encontrada");
                });

        // Validar nome duplicado (se mudou)
        if (request.getMuscleGroup() != null
                && !request.getMuscleGroup().equals(category.getMuscleGroup())
                && queryPort.findByName(request.getMuscleGroup()).isPresent()) {
            throw new BadRequestException("Categoria com este nome já existe");
        }

        // Atualizar campos
        category.updateFrom(request);

        ExerciseCategory updatedCategory = commandPort.update(category);

        log.info("Categoria atualizada com sucesso: {}", updatedCategory.getMuscleGroup());

        return mapper.toResponse(updatedCategory);
    }
}
