package backend.exercise.usecase;

import backend.exercise.dto.ExerciseResponse;
import backend.exercise.dto.ExerciseUpdateRequest;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.exercise.port.ExerciseCommandPort;
import backend.exercise.port.ExerciseQueryPort;
import backend.exercise.port.ExerciseValidationPort;
import backend.infrastructure.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("exerciseUpdateExerciseUseCase")
@Slf4j
@RequiredArgsConstructor
public class UpdateExerciseUseCase {

    private final ExerciseQueryPort queryPort;
    private final ExerciseCommandPort commandPort;
    private final ExerciseValidationPort validationPort;
    private final ExerciseCategoryQueryPort categoryQueryPort;
    private final ExerciseMapper mapper;

    @Transactional
    public ExerciseResponse execute(Long id, ExerciseUpdateRequest request){
        log.info("Atualizando exercício com ID: {}", id);

        Exercise exercise = queryPort.findByIdWithCategory(id)
                .orElseThrow(() -> {
                    log.warn("Exercício não encontrado para ID: {}", id);
                    return new BadRequestException("Exercício não encontrado");
                });

        // Validar nome duplicado (se mudou)
        if (request.getName() != null
                && !request.getName().equals(exercise.getName())
                && validationPort.existsByName(request.getName())) {
            throw new BadRequestException("Exercício com este nome já existe");
        }

        // Atualizar campos básicos
        exercise.updateFrom(request);

        // Atualizar categoria (se veio)
        if (request.getCategoryId() != null) {
            ExerciseCategory category = categoryQueryPort.findById(request.getCategoryId())
                    .orElseThrow(() -> new BadRequestException("Categoria não encontrada"));

            if (!category.getActive()) {
                throw new BadRequestException("Não é possível associar a categoria inativa");
            }

            exercise.setCategory(category);
        }

        Exercise updatedExercise = commandPort.update(exercise);

        log.info("Exercício atualizado com sucesso: {}", updatedExercise.getName());

        return mapper.toResponse(updatedExercise);
    }
}
