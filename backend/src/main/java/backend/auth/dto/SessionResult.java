package backend.auth.dto;

import backend.user.dto.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionResult {

    private UserResponse user;
    private String accessToken;
    private String refreshToken;
}

