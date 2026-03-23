package backend.exercise.dto;

import backend.exercise.model.entity.Exercise;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseCategoryResponse {

    private Long id;

    private String muscleGroup;

    private String description;

    private Boolean active;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
