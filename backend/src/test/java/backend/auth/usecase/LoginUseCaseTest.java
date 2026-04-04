package backend.auth.usecase;

import backend.auth.dto.LoginRequest;
import backend.auth.dto.LoginResponse;
import backend.infrastructure.exception.UnauthorizedException;
import backend.infrastructure.security.JwtTokenProvider;
import backend.user.dto.UserResponse;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
import backend.user.model.valueObjects.Email;
import backend.user.model.valueObjects.Password;
import backend.user.port.UserQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private LoginUseCase useCase;

    @Test
    void executeShouldReturnTokenAndUserWhenCredentialsAreValid() {
        LoginRequest request = LoginRequest.builder().email("john@example.com").password("12345678").build();
        User user = User.builder()
                .id(1L)
                .email(new Email("john@example.com"))
                .passwordHash(Password.fromHash("hashed"))
                .active(true)
                .build();
        UserResponse userResponse = UserResponse.builder().id(1L).name("John").build();

        when(userQueryPort.findByEmail(new Email("john@example.com"))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("12345678", "hashed")).thenReturn(true);
        when(jwtTokenProvider.generateToken(user)).thenReturn("jwt-token");
        when(mapper.toResponse(user)).thenReturn(userResponse);

        LoginResponse result = useCase.execute(request);

        assertEquals("jwt-token", result.getToken());
        assertSame(userResponse, result.getUser());
        verify(userQueryPort).findByEmail(new Email("john@example.com"));
    }

    @Test
    void executeShouldThrowWhenEmailIsNotFound() {
        LoginRequest request = LoginRequest.builder().email("none@example.com").password("12345678").build();
        when(userQueryPort.findByEmail(new Email("none@example.com"))).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> useCase.execute(request));

        assertEquals("Email ou senha inválidos", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenUserIsInactive() {
        LoginRequest request = LoginRequest.builder().email("inactive@example.com").password("12345678").build();
        User inactiveUser = User.builder()
                .id(2L)
                .email(new Email("inactive@example.com"))
                .passwordHash(Password.fromHash("hashed"))
                .active(false)
                .build();

        when(userQueryPort.findByEmail(new Email("inactive@example.com"))).thenReturn(Optional.of(inactiveUser));

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> useCase.execute(request));

        assertEquals("Usuário inativo", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenPasswordDoesNotMatch() {
        LoginRequest request = LoginRequest.builder().email("john@example.com").password("wrong").build();
        User user = User.builder()
                .id(1L)
                .email(new Email("john@example.com"))
                .passwordHash(Password.fromHash("hashed"))
                .active(true)
                .build();

        when(userQueryPort.findByEmail(new Email("john@example.com"))).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> useCase.execute(request));

        assertEquals("Email ou senha inválidos", ex.getMessage());
    }
}

