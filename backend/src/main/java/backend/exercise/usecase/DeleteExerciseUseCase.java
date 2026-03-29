package backend.exercise.usecase;

import backend.exercise.model.entity.Exercise;
import backend.exercise.port.ExerciseCommandPort;
import backend.exercise.port.ExerciseQueryPort;
import backend.exercise.port.ExerciseUsagePort;
import backend.infrastructure.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("exerciseDeleteExerciseUseCase")
@Slf4j
@RequiredArgsConstructor
public class DeleteExerciseUseCase {

    private final ExerciseQueryPort queryPort;
    private final ExerciseCommandPort commandPort;
    private final ExerciseUsagePort exerciseUsagePort;

    @Transactional
    public void execute(Long id){
        log.info("Deletando permanentemente exercício com ID: {}", id);

        Exercise exercise = queryPort.findByIdWithCategory(id)
                .orElseThrow(() -> {
                    log.warn("Exercício não encontrado para ID: {}", id);
                    return new BadRequestException("Exercício não encontrado");
                });
        boolean isInUse = exerciseUsagePort.isExerciseInUse(exercise.getId());

        if (isInUse) {
            throw new BadRequestException("Não é possível deletar exercício em uso");
        }

        commandPort.deleteById(exercise.getId());

        log.warn("Exercício deletado: {}", exercise.getName());
    }
}
