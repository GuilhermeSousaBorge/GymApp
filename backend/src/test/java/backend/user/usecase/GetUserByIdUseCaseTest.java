package backend.user.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.user.dto.UserResponse;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetUserByIdUseCaseTest {

    @Mock
    private UserQueryPort queryPort;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private GetUserByIdUseCase useCase;

    @Test
    void executeShouldReturnMappedResponseWhenUserExists() {
        Long userId = 1L;
        User user = User.builder().id(userId).name("Maria").build();
        UserResponse response = UserResponse.builder().id(userId).name("Maria").build();

        when(queryPort.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(response);

        UserResponse result = useCase.execute(userId);

        assertSame(response, result);
        verify(queryPort).findById(userId);
        verify(mapper).toResponse(user);
    }

    @Test
    void executeShouldThrowWhenUserDoesNotExist() {
        Long userId = 99L;
        when(queryPort.findById(userId)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(userId));

        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(queryPort).findById(userId);
    }
}

