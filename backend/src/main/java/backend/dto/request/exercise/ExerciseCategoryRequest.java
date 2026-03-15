package backend.dto.request.exercise;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseCategoryRequest {

    @NotBlank(message = "O grupo muscular é obrigatório.")
    @Size(max = 150, message = "O grupo muscular deve ter no máximo 150 caracteres.")
    private String muscleGroup;

    private String description;
}
