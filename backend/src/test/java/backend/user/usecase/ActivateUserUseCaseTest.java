package backend.user.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.user.model.entity.User;
import backend.user.port.UserCommandPort;
import backend.user.port.UserQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivateUserUseCaseTest {

    @Mock
    private UserQueryPort queryPort;

    @Mock
    private UserCommandPort commandPort;

    @InjectMocks
    private ActivateUserUseCase useCase;

    @Test
    void executeShouldActivateUserWhenFound() {
        Long userId = 5L;
        User user = User.builder().id(userId).active(false).build();

        when(queryPort.findById(userId)).thenReturn(Optional.of(user));

        assertFalse(user.getActive());
        useCase.execute(userId);

        assertTrue(user.getActive());
        verify(commandPort).update(user);
    }

    @Test
    void executeShouldThrowWhenUserDoesNotExist() {
        Long userId = 44L;
        when(queryPort.findById(userId)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(userId));

        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(queryPort).findById(userId);
    }
}

