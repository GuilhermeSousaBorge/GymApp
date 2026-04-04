package backend.auth.controller;

import backend.auth.dto.LoginRequest;
import backend.auth.dto.RegisterRequest;
import backend.auth.dto.LoginResponse;
import backend.auth.usecase.LoginUseCase;
import backend.auth.usecase.MeUseCase;
import backend.auth.usecase.RegisterUseCase;
import backend.user.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * CAMADA: PRESENTATION - Controller
 *
 * Porta de entrada da aplicação (REST API)
 * Define os endpoints HTTP para autenticação
 *
 * @RestController: Marca como controller REST (retorna JSON)
 * @RequestMapping: Define rota base (/api/auth)
 * @RequiredArgsConstructor: Lombok injeta dependências
 * @Slf4j: Logger
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final MeUseCase meUseCase;

    /**
     * ENDPOINT: POST /api/auth/login
     *
     * Login de usuário
     *
     * REQUEST BODY:
     * {
     *   "email": "admin@academia.com",
     *   "password": "admin123"
     * }
     *
     * RESPONSE: 200 OK
     * {
     *   "user": {
     *     "id": 1,
     *     "name": "João Silva",
     *     "email": "admin@academia.com",
     *     "role": {...}
     *   },
     *   "token": "eyJhbGciOiJIUzI1NiJ9..."
     * }
     *
     * RESPONSE: 401 UNAUTHORIZED
     * {
     *   "message": "Email ou senha inválidos"
     * }
     *
     * @Valid: Valida o DTO automaticamente (Bean Validation)
     * @RequestBody: Converte JSON do body para objeto Java
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Email: {}", request.getEmail());

        LoginResponse login = loginUseCase.execute(request);

        return ResponseEntity.ok(login);  // HTTP 200 OK
    }

    /**
     * ENDPOINT: POST /api/auth/register
     *
     * Registro de novo usuário
     *
     * REQUEST BODY:
     * {
     *   "name": "Maria Silva",
     *   "email": "maria@email.com",
     *   "password": "senha123",
     *   "gender": "FEMALE",
     *   "cpf": "123.456.789-00",
     *   "phone": "(34) 99999-0000",
     *   "birthDate": "1995-05-15"
     * }
     *
     * RESPONSE: 201 CREATED
     * {
     *   "user": {...},
     *   "token": "..."
     * }
     *
     * RESPONSE: 400 BAD REQUEST
     * {
     *   "message": "Email já cadastrado"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("POST /api/auth/register - Email: {}", request.getEmail());

        LoginResponse register = registerUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED)  // HTTP 201 CREATED
                .body(register);
    }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> me(Authentication authentication){
        return ResponseEntity.ok(meUseCase.execute(authentication));
    }

    /**
     * ENDPOINT: POST /api/auth/logout
     *
     * Logout (invalidar token)
     *
     * OBS: Com JWT stateless, o logout é feito no frontend
     * (remove token do localStorage)
     *
     * Backend pode implementar blacklist de tokens se necessário
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        log.info("POST /api/auth/logout");
        return ResponseEntity.noContent().build();  // HTTP 204 NO CONTENT
    }

}
