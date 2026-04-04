package backend.exercise.usecase;

import backend.exercise.dto.ExerciseRequest;
import backend.exercise.dto.ExerciseResponse;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.exercise.port.ExerciseCommandPort;
import backend.exercise.port.ExerciseValidationPort;
import backend.infrastructure.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("exerciseCreateExerciseUseCase")
@Slf4j
@RequiredArgsConstructor
public class CreateExerciseUseCase {

    private final ExerciseCommandPort commandPort;
    private final ExerciseValidationPort validationPort;
    private final ExerciseCategoryQueryPort categoryQueryPort;
    private final ExerciseMapper mapper;

    @Transactional
    public ExerciseResponse execute(ExerciseRequest request){
        log.info("Criando exercício: {}", request.getName());

        // Validar duplicação
        if (validationPort.existsByName(request.getName())) {
            throw new BadRequestException("Exercício com este nome já existe");
        }

        // Buscar categoria
        ExerciseCategory category = categoryQueryPort.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Categoria não encontrada"));

        // Verificar se categoria está ativa
        if (!category.getActive()) {
            throw new BadRequestException("Não é possível criar exercício em categoria inativa");
        }

        Exercise exercise = Exercise.builder()
                .name(request.getName())
                .description(request.getDescription())
                .equipment(request.getEquipment())
                .videoUrl(request.getVideoUrl())
                .category(category)
                .active(true)
                .build();

        Exercise savedExercise = commandPort.save(exercise);

        log.info("Exercício criado com sucesso: {} (ID {})",
                savedExercise.getName(), savedExercise.getId());

        return mapper.toResponse(savedExercise);
    }
}
