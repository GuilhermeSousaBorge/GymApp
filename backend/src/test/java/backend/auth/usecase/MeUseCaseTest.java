package backend.auth.usecase;

import backend.infrastructure.exception.UnauthorizedException;
import backend.user.dto.UserResponse;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserMapper mapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MeUseCase useCase;

    @Test
    void executeShouldReturnCurrentUserWhenPrincipalIsLong() {
        User user = User.builder().id(1L).name("Ana").build();
        UserResponse response = UserResponse.builder().id(1L).name("Ana").build();

        when(authentication.getPrincipal()).thenReturn(1L);
        when(userQueryPort.findById(1L)).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(response);

        UserResponse result = useCase.execute(authentication);

        assertSame(response, result);
        verify(userQueryPort).findById(1L);
    }

    @Test
    void executeShouldThrowWhenPrincipalIsNotLong() {
        when(authentication.getPrincipal()).thenReturn("1");

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> useCase.execute(authentication));

        assertEquals("Usuário não autenticado", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenUserIsNotFound() {
        when(authentication.getPrincipal()).thenReturn(99L);
        when(userQueryPort.findById(99L)).thenReturn(Optional.empty());

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> useCase.execute(authentication));

        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}

