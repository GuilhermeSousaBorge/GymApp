package backend.user.dto;

import backend.user.model.enums.Gender;
import backend.user.model.interfaces.UserUpdatable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest implements UserUpdatable {

    private String name;

    @Email(message = "Email inválido")
    private String email;

    private String cpf;

    private String phone;

    private Gender gender;

    private LocalDate birthDate;

    private Long roleId;

    private AddressUpdateRequest address;
}
