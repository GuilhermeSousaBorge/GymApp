package backend.mapper;

import backend.dto.response.exercise.ExerciseResponse;
import backend.model.entity.Exercise;
import org.springframework.stereotype.Component;

@Component
public class ExerciseMapper {

    public ExerciseResponse toResponse(Exercise exercise) {
        if(exercise == null) return null;

        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .equipment(exercise.getEquipment())
                .videoUrl(exercise.getVideoUrl())
                .active(exercise.getActive())
                .categoryId(exercise.getCategory() != null ? exercise.getCategory().getId() : null)
                .category(toCategoryinfo(exercise))
                .createdAt(exercise.getCreatedAt())
                .updatedAt(exercise.getUpdatedAt())
                .build();
    }

    private ExerciseResponse.CategoryInfo toCategoryinfo(Exercise exercise){
        if(exercise.getCategory() == null) return null;

        return ExerciseResponse.CategoryInfo.builder()
                .id(exercise.getCategory().getId())
                .muscleGroup(exercise.getCategory().getMuscleGroup())
                .description(exercise.getCategory().getDescription())
                .active(exercise.getCategory().getActive())
                .build();

    }
}
