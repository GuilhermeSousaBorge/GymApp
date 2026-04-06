package backend.auth.usecase;

import backend.auth.dto.RegisterRequest;
import backend.auth.dto.SessionResult;
import backend.infrastructure.exception.BadRequestException;
import backend.user.dto.UserResponse;
import backend.user.model.entity.Role;
import backend.user.model.entity.User;
import backend.user.model.enums.Gender;
import backend.user.model.valueObjects.Email;
import backend.user.model.valueObjects.Password;
import backend.user.port.RolePort;
import backend.user.port.UserCommandPort;
import backend.user.port.UserValidationPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUseCaseTest {

    @Mock
    private UserValidationPort userValidationPort;

    @Mock
    private UserCommandPort userCommandPort;

    @Mock
    private RolePort rolePort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CreateSessionUseCase createSessionUseCase;

    @InjectMocks
    private RegisterUseCase useCase;

    @Test
    void executeShouldRegisterUserWhenRequestIsValid() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Maria")
                .email("maria@example.com")
                .password("12345678")
                .gender(Gender.FEMALE)
                .build();

        Role alunoRole = Role.builder().id(1L).name("Aluno").build();

        User saved = User.builder()
                .id(10L)
                .name("Maria")
                .email(new Email("maria@example.com"))
                .passwordHash(Password.fromHash("hashed-password"))
                .active(true)
                .role(alunoRole)
                .gender(Gender.FEMALE)
                .build();

        UserResponse responseUser = UserResponse.builder().id(10L).name("Maria").build();

        when(userValidationPort.existsByEmail(new Email("maria@example.com"))).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("hashed-password");
        when(rolePort.findByName("Aluno")).thenReturn(Optional.of(alunoRole));
        when(userCommandPort.save(any(User.class))).thenReturn(saved);
        when(createSessionUseCase.execute(saved)).thenReturn(SessionResult.builder()
                .user(responseUser)
                .accessToken("jwt-register")
                .refreshToken("refresh-register")
                .build());

        SessionResult result = useCase.execute(request);

        assertEquals("jwt-register", result.getAccessToken());
        assertSame(responseUser, result.getUser());
        verify(userCommandPort).save(any(User.class));
    }

    @Test
    void executeShouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Maria")
                .email("maria@example.com")
                .password("12345678")
                .gender(Gender.FEMALE)
                .build();

        when(userValidationPort.existsByEmail(new Email("maria@example.com"))).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Email já cadastrado", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenDefaultRoleIsMissing() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Maria")
                .email("maria@example.com")
                .password("12345678")
                .gender(Gender.FEMALE)
                .build();

        when(userValidationPort.existsByEmail(new Email("maria@example.com"))).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("hashed-password");
        when(rolePort.findByName("Aluno")).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> useCase.execute(request));

        assertEquals("Role Aluno não encontrada", ex.getMessage());
    }
}

