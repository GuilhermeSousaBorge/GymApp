package backend.dto.request.training;

import backend.model.enums.DayOfWeek;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingSheetUpdateRequest {


    @Size(min = 1, max = 150, message = "O nome deve conter entre 1 e 150 caracteres")
    private String name;

    private String description;

    private Integer restTimeSeconds;

    private Integer orderInProgram;

    @Size(min = 1, max = 7, message = "Deve haver entre 1 e 7 dias da semana")
    private Set<DayOfWeek> weekdays;

    private Boolean active;
}
