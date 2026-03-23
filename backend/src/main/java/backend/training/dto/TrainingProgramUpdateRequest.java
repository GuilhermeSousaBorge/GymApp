package backend.training.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingProgramUpdateRequest {

    @Size(max = 150, message = "O nome do programa de treinamento deve ter no máximo 150 caracteres.")
    private String name;

    @Size(max = 150, message = "A descrição do programa de treinamento deve ter no máximo 150 caracteres.")
    private String description;

//    private Integer programOrder;

}

