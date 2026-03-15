package backend.dto.response.exercise;

import backend.model.entity.Exercise;
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
