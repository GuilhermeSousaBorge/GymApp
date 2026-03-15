package backend.dto.request.training;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingExerciseRequest {

    @NotBlank(message = "As repetições do exercício são obrigatórias.")
    @Size(max = 50)
    private String reps;

    @NotNull(message = "O número de séries do exercício é obrigatório.")
    private Integer sets;

    @NotNull(message = "O tempo de descanso entre as séries é obrigatório.")
    private Integer restTimeInSeconds;

    @Size(max = 100, message = "As notas de técnica devem ter no máximo 100 caracteres.")
    private String techniqueNotes;

    @NotNull(message = "O exercício é obrigatório.")
    private Long exerciseId;

    @NotNull(message = "A ficha de treino é obrigatório.")
    private Long trainingSheetId;
}
