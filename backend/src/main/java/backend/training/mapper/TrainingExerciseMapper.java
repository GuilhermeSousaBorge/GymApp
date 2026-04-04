package backend.training.mapper;

import backend.training.dto.TrainingExerciseResponse;
import backend.training.model.entity.TrainingExercise;
import org.springframework.stereotype.Component;

@Component
public class TrainingExerciseMapper {

    public TrainingExerciseResponse toResponse(TrainingExercise trainingExercise) {
        if(trainingExercise == null) return null;

        return TrainingExerciseResponse.builder()
                .id(trainingExercise.getId())
                .sets(trainingExercise.getSets())
                .reps(trainingExercise.getReps())
                .restTimeInSeconds(trainingExercise.getRestTimeInSeconds())
                .orderInSheet(trainingExercise.getOrderInSheet())
                .exerciseInfo(toExerciseInfo(trainingExercise.getExercise()))
                .exerciseId(trainingExercise.getExercise().getId())
                .trainingSheetId(trainingExercise.getTrainingSheet().getId())
                .createdAt(trainingExercise.getCreatedAt())
                .updatedAt(trainingExercise.getUpdatedAt())
                .build();
    }

    private TrainingExerciseResponse.ExerciseInfo toExerciseInfo(backend.exercise.model.entity.Exercise exercise) {
        if(exercise == null) return null;

        return TrainingExerciseResponse.ExerciseInfo.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .equipment(exercise.getEquipment())
                .category(toCategoryInfo(exercise.getCategory()))
                .build();
    }

    private TrainingExerciseResponse.ExerciseInfo.CategoryInfo toCategoryInfo(backend.exercise.model.entity.ExerciseCategory category) {
        if (category == null) return null;

        return TrainingExerciseResponse.ExerciseInfo.CategoryInfo.builder()
                .id(category.getId())
                .muscleGroup(category.getMuscleGroup())
                .build();
    }
}
