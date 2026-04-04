package backend.plan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanRequest {

    @NotBlank(message = "O nome do plano e obrigatorio.")
    @Size(max = 100, message = "O nome do plano deve ter no maximo 100 caracteres.")
    private String name;

    @Size(max = 1000, message = "A descricao do plano deve ter no maximo 1000 caracteres.")
    private String description;

    @NotNull(message = "O preco do plano e obrigatorio.")
    @DecimalMin(value = "0.00", inclusive = true, message = "O preco do plano nao pode ser negativo.")
    private BigDecimal price;

    @NotNull(message = "O limite maximo de alunos e obrigatorio.")
    @Min(value = 0, message = "O limite maximo de alunos deve ser maior ou igual a zero.")
    private Integer maxStudents;

    @NotNull(message = "O limite maximo de programas e obrigatorio.")
    @Min(value = 1, message = "O limite maximo de programas deve ser maior que zero.")
    private Integer maxPrograms;

    private Set<String> benefits;
}

