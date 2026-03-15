package backend.dto.request.exercise;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseCategoryUpdateRequest {

    @Size(max = 150)
    private String muscleGroup;

    private String description;

    private Boolean active;
}
