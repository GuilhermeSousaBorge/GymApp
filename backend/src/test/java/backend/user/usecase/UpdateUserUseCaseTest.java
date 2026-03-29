package backend.user.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.user.dto.AddressUpdateRequest;
import backend.user.dto.UserResponse;
import backend.user.dto.UserUpdateRequest;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.Role;
import backend.user.model.entity.User;
import backend.user.model.valueObjects.Cpf;
import backend.user.model.valueObjects.Email;
import backend.user.port.RolePort;
import backend.user.port.UserCommandPort;
import backend.user.port.UserQueryPort;
import backend.user.port.UserValidationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserCommandPort userCommandPort;

    @Mock
    private UserValidationPort userValidationPort;

    @Mock
    private RolePort rolePort;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private UpdateUserUseCase useCase;

    @Test
    void executeShouldUpdateUserWhenRequestIsValid() {
        Long userId = 1L;
        Long newRoleId = 2L;

        User user = User.builder()
                .id(userId)
                .name("Old Name")
                .email(new Email("old@example.com"))
                .cpf(new Cpf("12345678901"))
                .phone("1111")
                .active(true)
                .build();

        Role newRole = Role.builder().id(newRoleId).name("PersonalTrainer").build();

        UserUpdateRequest request = UserUpdateRequest.builder()
                .name("New Name")
                .email("new@example.com")
                .cpf("98765432100")
                .phone("9999")
                .roleId(newRoleId)
                .address(new AddressUpdateRequest(10, "30123-000", "Centro", "Rua A", "Belo Horizonte", "MG"))
                .build();

        UserResponse response = UserResponse.builder().id(userId).name("New Name").build();

        when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));
        when(userValidationPort.existsByEmailAndIdNot(new Email("new@example.com"), userId)).thenReturn(false);
        when(userValidationPort.existsByCpfAndIdNot(new Cpf("98765432100"), userId)).thenReturn(false);
        when(rolePort.findById(newRoleId)).thenReturn(Optional.of(newRole));
        when(userCommandPort.update(user)).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(response);

        UserResponse result = useCase.execute(userId, request);

        assertSame(response, result);
        assertEquals("New Name", user.getName());
        assertEquals("new@example.com", user.getEmail().getValue());
        assertEquals("98765432100", user.getCpf().getValue());
        assertEquals("9999", user.getPhone());
        assertSame(newRole, user.getRole());
        assertNotNull(user.getAddress());
        assertEquals("Belo Horizonte", user.getAddress().getCity());

        verify(userCommandPort).update(user);
        verify(mapper).toResponse(user);
    }

    @Test
    void executeShouldThrowWhenUserDoesNotExist() {
        Long userId = 100L;
        UserUpdateRequest request = UserUpdateRequest.builder().name("Any").build();
        when(userQueryPort.findById(userId)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(userId, request));

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenEmailAlreadyExists() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        UserUpdateRequest request = UserUpdateRequest.builder().email("duplicate@example.com").build();

        when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));
        when(userValidationPort.existsByEmailAndIdNot(new Email("duplicate@example.com"), userId)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(userId, request));

        assertEquals("Email já cadastrado", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenCpfAlreadyExists() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();
        UserUpdateRequest request = UserUpdateRequest.builder().cpf("12345678901").build();

        when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));
        when(userValidationPort.existsByCpfAndIdNot(new Cpf("12345678901"), userId)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(userId, request));

        assertEquals("CPF já cadastrado", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenRoleIdIsInvalid() {
        Long userId = 1L;
        Long invalidRoleId = 999L;
        User user = User.builder().id(userId).build();
        UserUpdateRequest request = UserUpdateRequest.builder().roleId(invalidRoleId).build();

        when(userQueryPort.findById(userId)).thenReturn(Optional.of(user));
        when(rolePort.findById(invalidRoleId)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(userId, request));

        assertEquals("Role inválida", ex.getMessage());
    }
}

