package backend.user.controller;

import backend.user.dto.UserUpdateRequest;
import backend.user.dto.UserResponse;
import backend.infrastructure.security.IsAdmin;
import backend.user.usecase.ActivateUserUseCase;
import backend.user.usecase.DeactivateUserUseCase;
import backend.user.usecase.GetUserByIdUseCase;
import backend.user.usecase.ListUserUseCase;
import backend.user.usecase.UpdateUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()") // Exige autenticação para acessar os endpoints
public class UserController {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final ListUserUseCase listUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final ActivateUserUseCase activateUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Administrador') or hasRole('PersonalTrainer') or #id == authentication.principal")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        // Lógica para buscar usuário por ID
        return ResponseEntity.ok().body(getUserByIdUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers(
            Authentication authentication
    ) {
        // Lógica para listar usuários
        return ResponseEntity.ok().body(listUserUseCase.execute(authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrador') or hasRole('PersonalTrainer') or #id == authentication.principal")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateRequest userRequest) {
        // Lógica para atualizar usuário
        return ResponseEntity.ok().body(updateUserUseCase.execute(id, userRequest));
    }


    @PutMapping("/{id}/deactivate")
    @IsAdmin
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        deactivateUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/activate")
    @IsAdmin
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        activateUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
