package backend.dto.request.user;

import backend.model.enums.Gender;
import backend.model.interfaces.UserUpdatable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest implements UserUpdatable {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @Email(message = "Email inválido")
    @NotBlank(message = "O email é obrigatório")
    private String email;

    private String cpf;

    private String phone;

    @NotNull(message = "O gênero é obrigatório")
    private Gender gender;

    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate birthDate;

    private Long roleId;

    private Long addressId;

}
