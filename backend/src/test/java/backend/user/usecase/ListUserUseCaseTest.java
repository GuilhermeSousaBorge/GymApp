package backend.user.usecase;

import backend.infrastructure.security.JwtTokenProvider;
import backend.user.dto.UserResponse;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
import backend.user.model.enums.Roles;
import backend.user.port.UserQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListUserUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserMapper mapper;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ListUserUseCase useCase;

    @Test
    void executeShouldListOnlyPersonalsWhenTokenRoleIsAluno() {
        String token = "token-aluno";
        User user = User.builder().id(10L).name("Personal A").build();
        UserResponse response = UserResponse.builder().id(10L).name("Personal A").build();

        when(authentication.getCredentials()).thenReturn(token);
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn(Roles.ALUNO.getRole());
        when(userQueryPort.findByRole(Roles.PERSONAL.getRole())).thenReturn(List.of(user));
        when(mapper.toResponse(user)).thenReturn(response);

        List<UserResponse> result = useCase.execute(authentication);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        verify(userQueryPort).findByRole(Roles.PERSONAL.getRole());
        verify(userQueryPort, never()).findAll();
    }

    @Test
    void executeShouldListAllUsersWhenTokenRoleIsNotAluno() {
        String token = "token-admin";
        User user1 = User.builder().id(1L).name("A").build();
        User user2 = User.builder().id(2L).name("B").build();

        when(authentication.getCredentials()).thenReturn(token);
        when(jwtTokenProvider.getRoleFromToken(token)).thenReturn(Roles.ADMINISTRADOR.getRole());
        when(userQueryPort.findAll()).thenReturn(List.of(user1, user2));
        when(mapper.toResponse(user1)).thenReturn(UserResponse.builder().id(1L).name("A").build());
        when(mapper.toResponse(user2)).thenReturn(UserResponse.builder().id(2L).name("B").build());

        List<UserResponse> result = useCase.execute(authentication);

        assertEquals(2, result.size());
        verify(userQueryPort).findAll();
        verify(userQueryPort, never()).findByRole(Roles.PERSONAL.getRole());
    }
}

