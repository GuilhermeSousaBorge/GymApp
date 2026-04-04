package backend.exercise.usecase;

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
public class DeleteCategoryUseCase {

    private final ExerciseCategoryQueryPort queryPort;
    private final ExerciseCategoryCommandPort commandPort;

    @Transactional
    public void execute(Long id){
        log.info("Deletando permanentemente categoria com ID: {}", id);

        ExerciseCategory category = queryPort.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada para ID: {}", id);
                    return new BadRequestException("Categoria não encontrada");
                });

        // TODO: Verificar se tem exercícios associados antes de deletar
        if (category.getExercises() != null && !category.getExercises().isEmpty()) {
            throw new BadRequestException("Não é possível deletar categoria com exercícios associados");
        }

        commandPort.deleteById(category.getId());

        log.warn("Categoria deletada: {}", category.getMuscleGroup());
    }
}
