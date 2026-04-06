package backend.auth.controller;

import backend.auth.dto.LoginRequest;
import backend.auth.dto.LoginResponse;
import backend.auth.dto.EmailRequest;
import backend.auth.dto.MessageResponse;
import backend.auth.dto.PasswordResetRequest;
import backend.auth.dto.RefreshTokenResponse;
import backend.auth.dto.RegisterRequest;
import backend.auth.dto.SessionResult;
import backend.auth.dto.TokenRequest;
import backend.auth.usecase.LoginUseCase;
import backend.auth.usecase.LogoutUseCase;
import backend.auth.usecase.MeUseCase;
import backend.auth.usecase.RefreshSessionUseCase;
import backend.auth.usecase.RequestEmailVerificationUseCase;
import backend.auth.usecase.RequestPasswordResetUseCase;
import backend.auth.usecase.ResetPasswordUseCase;
import backend.auth.usecase.RegisterUseCase;
import backend.auth.usecase.VerifyEmailUseCase;
import backend.infrastructure.exception.UnauthorizedException;
import backend.user.dto.UserResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;
    private final MeUseCase meUseCase;
    private final RefreshSessionUseCase refreshSessionUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RequestEmailVerificationUseCase requestEmailVerificationUseCase;
    private final VerifyEmailUseCase verifyEmailUseCase;
    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final ResetPasswordUseCase resetPasswordUseCase;

    @Value("${jwt.refresh-expiration:604800000}")
    private Long refreshTokenExpirationMs;

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${cookie.same-site:Lax}")
    private String cookieSameSite;

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
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {
        log.info("POST /api/auth/login - Email: {}", request.getEmail());

        SessionResult session = loginUseCase.execute(request);
        setRefreshTokenCookie(response, session.getRefreshToken());

        LoginResponse login = LoginResponse.builder()
                .user(session.getUser())
                .token(session.getAccessToken())
                .build();

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
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request,
                                                  HttpServletResponse response) {
        log.info("POST /api/auth/register - Email: {}", request.getEmail());

        SessionResult session = registerUseCase.execute(request);
        setRefreshTokenCookie(response, session.getRefreshToken());

        LoginResponse register = LoginResponse.builder()
                .user(session.getUser())
                .token(session.getAccessToken())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)  // HTTP 201 CREATED
                .body(register);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(HttpServletRequest request,
                                                        HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        SessionResult session = refreshSessionUseCase.execute(refreshToken);

        setRefreshTokenCookie(response, session.getRefreshToken());

        return ResponseEntity.ok(RefreshTokenResponse.builder()
                .token(session.getAccessToken())
                .build());
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
    public ResponseEntity<Void> logout(HttpServletRequest request,
                                       HttpServletResponse response) {
        log.info("POST /api/auth/logout");

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                    logoutUseCase.execute(cookie.getValue());
                }
            }
        }

        clearRefreshTokenCookie(response);
        return ResponseEntity.noContent().build();  // HTTP 204 NO CONTENT
    }

    @PostMapping("/email/verification/send")
    public ResponseEntity<MessageResponse> sendEmailVerification(@Valid @RequestBody EmailRequest request) {
        requestEmailVerificationUseCase.execute(request.getEmail());
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Se o email existir e estiver pendente de confirmação, o link foi enviado.")
                .build());
    }

    @PostMapping("/email/verification/confirm")
    public ResponseEntity<MessageResponse> confirmEmailVerification(@Valid @RequestBody TokenRequest request) {
        verifyEmailUseCase.execute(request.getToken());
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Email verificado com sucesso.")
                .build());
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody EmailRequest request) {
        requestPasswordResetUseCase.execute(request.getEmail());
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Se o email existir, você receberá instruções para redefinir sua senha.")
                .build());
    }

    @PostMapping("/password/reset")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        resetPasswordUseCase.execute(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(MessageResponse.builder()
                .message("Senha redefinida com sucesso.")
                .build());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api/auth")
                .maxAge(refreshTokenExpirationMs / 1000)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api/auth")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new UnauthorizedException("Refresh token não encontrado");
        }

        for (Cookie cookie : cookies) {
            if ("refresh_token".equals(cookie.getName()) && cookie.getValue() != null && !cookie.getValue().isBlank()) {
                return cookie.getValue();
            }
        }

        throw new UnauthorizedException("Refresh token não encontrado");
    }

}
