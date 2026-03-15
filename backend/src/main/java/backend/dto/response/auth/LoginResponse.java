package backend.dto.response.auth;

import backend.dto.response.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CAMADA: APPLICATION - DTO Response
 *
 * Representa a resposta após login bem-sucedido
 * Contém o token JWT e os dados do usuário
 *
 * ALINHADO COM O FRONTEND:
 * { user: {...}, token: "..." }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private UserResponse user;  // Dados do usuário
    private String token;       // Token JWT para autenticação
}
