package backend.controller;

import backend.dto.request.user.UserRequest;
import backend.dto.request.user.UserUpdateRequest;
import backend.dto.response.user.UserResponse;
import backend.infrastructure.security.IsAdmin;
import backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()") // Exige autenticação para acessar os endpoints
public class UserController {

    private final UserService userService;


    public void createUser() {
        // Lógica para criar usuário
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('Administrador') or hasRole('PersonalTrainer') or #id == authentication.principal")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        // Lógica para buscar usuário por ID
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers(
            Authentication authentication
    ) {
        // Lógica para listar usuários
        return ResponseEntity.ok().body(userService.listUsers(authentication));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('Administrador') or hasRole('PersonalTrainer') or #id == authentication.principal")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,@Valid @RequestBody UserUpdateRequest userRequest) {
        // Lógica para atualizar usuário
        return ResponseEntity.ok().body(userService.updateUser(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // Lógica para deletar usuário
        userService.deactivateUser(id);
        return ResponseEntity.noContent().build();
    }
}
