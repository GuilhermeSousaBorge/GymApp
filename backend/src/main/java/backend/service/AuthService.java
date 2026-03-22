package backend.service;

import backend.dto.request.auth.LoginRequest;
import backend.dto.request.auth.RegisterRequest;
import backend.dto.response.auth.LoginResponse;
import backend.dto.response.user.UserResponse;
import backend.model.entity.Role;
import backend.model.entity.User;
import backend.model.valueObjects.Email;
import backend.model.valueObjects.Password;
import backend.repository.RoleRepository;
import backend.repository.UserRepository;
import backend.infrastructure.exception.BadRequestException;
import backend.infrastructure.exception.UnauthorizedException;
import backend.infrastructure.security.JwtTokenProvider;
import backend.mapper.UserMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * CAMADA: APPLICATION - Service
 *
 * Serviço responsável por autenticação e registro de usuários.
 *
 * Decisão arquitetural:
 * - Service concreto (sem interface)
 * - Simples, direto, fácil de testar
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    /**
     * LOGIN
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        log.info("Tentativa de login para email: {}", request.getEmail());

        Email email = new Email(request.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Login falhou: email não encontrado - {}", request.getEmail());
                    return new UnauthorizedException("Email ou senha inválidos");
                });

        if (!user.getActive()) {
            log.warn("Login falhou: usuário inativo - {}", request.getEmail());
            throw new UnauthorizedException("Usuário inativo");
        }

        if (!user.isPasswordValid(request.getPassword(), passwordEncoder)) {
            log.warn("Login falhou: senha incorreta - {}", request.getEmail());
            throw new UnauthorizedException("Email ou senha inválidos");
        }

        String token = jwtTokenProvider.generateToken(user);

        log.info("Login bem-sucedido: {}", request.getEmail());

        return LoginResponse.builder()
                .user(userMapper.toResponse(user))
                .token(token)
                .build();
    }

    /**
     * REGISTER
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        log.info("Tentativa de registro para email: {}", request.getEmail());

        Email email = new Email(request.getEmail());

        if (userRepository.existsByEmail(email)) {
            log.warn("Registro falhou: email já existe - {}", request.getEmail());
            throw new BadRequestException("Email já cadastrado");
        }

        Password passwordHash = Password.create(request.getPassword(), passwordEncoder);

        Role defaultRole = roleRepository.findByName("Aluno") .orElseThrow(() -> new IllegalStateException("Role Aluno não encontrada"));

        User newUser = User.builder()
                .name(request.getName())
                .email(email)
                .passwordHash(passwordHash)
                .gender(request.getGender())
                .active(true)
                .role(defaultRole)
                .build();

        User savedUser = userRepository.save(newUser);

        log.info("Usuário registrado com sucesso: {} (ID {})",
                savedUser.getEmail(), savedUser.getId());

        String token = jwtTokenProvider.generateToken(savedUser);

        return LoginResponse.builder()
                .user(userMapper.toResponse(savedUser))
                .token(token)
                .build();
    }

    @Transactional
    public UserResponse me(Authentication authentication){

        var userId = authentication.getPrincipal();
        if(userId instanceof Long) {
            User user = userRepository.findById((Long) userId)
                    .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
            return userMapper.toResponse(user);
        }
        throw  new UnauthorizedException("Usuário não autenticado");
    }

}
