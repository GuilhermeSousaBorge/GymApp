package backend.exercise.dto;

import backend.exercise.model.interfaces.ExerciseCategoryUpdatable;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseCategoryUpdateRequest implements ExerciseCategoryUpdatable {

    @Size(max = 150)
    private String muscleGroup;

    private String description;

    private Boolean active;
}
