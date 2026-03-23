package backend.training.dto;

import backend.training.model.enums.DayOfWeek;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingSheetRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 1, max = 150, message = "O nome deve conter entre 1 e 150 caracteres")
    private String name;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    private Integer restTimeSeconds;

    @NotNull(message = "O ID do programa de treinamento é obrigatório")
    private Long trainingProgramId;

    @NotEmpty(message = "Dias da semana são obrigatórios")
    @Size(min = 1, max = 7, message = "Deve haver entre 1 e 7 dias da semana")
    private Set<DayOfWeek> weekDays;
}
