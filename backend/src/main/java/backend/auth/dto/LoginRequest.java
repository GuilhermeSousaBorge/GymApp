package backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * CAMADA: APPLICATION - DTO Request
 *
 * Representa os dados de entrada para login
 * Bean Validation valida os campos automaticamente
 *
 * POR QUÊ DTO?
 * - Não expõe a estrutura interna das entidades
 * - Permite validações específicas para cada operação
 * - Facilita versionamento da API
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
