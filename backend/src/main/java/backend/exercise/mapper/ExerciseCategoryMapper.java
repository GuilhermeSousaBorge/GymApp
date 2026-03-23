package backend.exercise.mapper;

import backend.exercise.dto.ExerciseCategoryResponse;
import backend.exercise.model.entity.ExerciseCategory;
import org.springframework.stereotype.Component;

@Component
public class ExerciseCategoryMapper {

    public ExerciseCategoryResponse toResponse(ExerciseCategory exerciseCategory) {
        if(exerciseCategory == null) {
            return null;
        }
        return ExerciseCategoryResponse.builder()
                .id(exerciseCategory.getId())
                .muscleGroup(exerciseCategory.getMuscleGroup())
                .description(exerciseCategory.getDescription())
                .active(exerciseCategory.getActive())
                .createdAt(exerciseCategory.getCreatedAt())
                .updatedAt(exerciseCategory.getUpdatedAt())
                .build();
    }
}
