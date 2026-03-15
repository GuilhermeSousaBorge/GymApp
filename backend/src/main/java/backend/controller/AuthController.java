package backend.controller;

import backend.dto.request.auth.LoginRequest;
import backend.dto.request.auth.RegisterRequest;
import backend.dto.response.auth.AuthResponse;
import backend.dto.response.auth.LoginResponse;
import backend.dto.response.user.UserResponse;
import backend.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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

    private final AuthService authService;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${cookie.secure}")
    private Boolean secure;

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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletResponse response) {
        log.info("POST /api/auth/login - Email: {}", request.getEmail());

        LoginResponse login = authService.login(request);

        addTokenCookie(response, login.getToken());

        AuthResponse authResponse = new AuthResponse(login.getUser());

        return ResponseEntity.ok(authResponse);  // HTTP 200 OK
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
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletResponse response) {
        log.info("POST /api/auth/register - Email: {}", request.getEmail());

        LoginResponse register = authService.register(request);

        addTokenCookie(response, register.getToken());

        AuthResponse authResponse = new AuthResponse(register.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)  // HTTP 201 CREATED
                .body(authResponse);
    }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> me(Authentication authentication){
        return ResponseEntity.ok(authService.me(authentication));
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
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        log.info("POST /api/auth/logout");

        clearTokenCookie(response);
        return ResponseEntity.noContent().build();  // HTTP 204 NO CONTENT
    }

    private void addTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(secure)       // true em produção
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtExpiration / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(secure)              // true em produção (https)
                .sameSite("Lax")            // ESSENCIAL p/ Next.js
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
