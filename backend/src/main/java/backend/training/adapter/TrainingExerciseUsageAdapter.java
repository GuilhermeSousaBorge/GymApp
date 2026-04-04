package backend.training.adapter;

import backend.exercise.port.ExerciseUsagePort;
import backend.training.repository.TrainingExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainingExerciseUsageAdapter implements ExerciseUsagePort {

    private final TrainingExerciseRepository trainingExerciseRepository;

    @Override
    public boolean isExerciseInUse(Long exerciseId) {
        log.debug("TrainingExerciseUsageAdapter: isExerciseInUse({})", exerciseId);
        return trainingExerciseRepository.existsByExerciseId(exerciseId);
    }
}

