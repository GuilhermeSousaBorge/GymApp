package backend.dto.request.auth;

import backend.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * CAMADA: APPLICATION - DTO Request
 *
 * Representa os dados de entrada para registro de novo usuário
 * Validações garantem dados consistentes antes de chegar ao service
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, message = "Nome deve conter no mínimo 3 caracteres")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    @NotNull(message = "Gênero é obrigatório")
    private Gender gender;

//    private String cpf;

//    private String phone;

//    private LocalDate birthDate;
}