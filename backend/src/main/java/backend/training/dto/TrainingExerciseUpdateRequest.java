package backend.training.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingExerciseUpdateRequest {

    @Size(max = 50)
    private String reps;

    private Integer sets;

    private Integer orderInSheet;

    private Integer restTimeInSeconds;

    @Size(max = 100, message = "As notas de técnica devem ter no máximo 100 caracteres.")
    private String techniqueNotes;

}
