package backend.training.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgramRequest {

    @NotBlank(message = "O nome do programa de treinamento é obrigatório.")
    @Size(max = 150, message = "O nome do programa de treinamento deve ter no máximo 150 caracteres.")
    private String name;

    @NotBlank(message = "A descrição do programa de treinamento é obrigatória.")
    @Size(max = 150, message = "A descrição do programa de treinamento deve ter no máximo 150 caracteres.")
    private String description;

//    @NotNull(message = "A ordem do programa de treinamento é obrigatória.")
//    private Integer programOrder;

    @NotNull(message = "O aluno é obrigatório.")
    private Long userId;

    private Long trainerId;
}
