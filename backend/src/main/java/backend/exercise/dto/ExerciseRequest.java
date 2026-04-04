package backend.exercise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
    private String name;

    private String description;

    @Size(max = 100)
    private String equipment;

    @Size(max = 255)
    private String videoUrl;

    @Size(max = 255)
    private String imageUrl;

    @NotNull(message = "Categoria é obrigatória")
    private Long categoryId;
}
