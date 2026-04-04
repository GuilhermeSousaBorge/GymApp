package backend.user.usecase;

import backend.infrastructure.security.JwtTokenProvider;
import backend.user.dto.UserResponse;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
import backend.user.model.enums.Roles;
import backend.user.port.UserQueryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ListUserUseCase {

    private final UserQueryPort userQueryPort;
    private final UserMapper mapper;
    private final JwtTokenProvider jwtTokenProvider;

    public ListUserUseCase(UserQueryPort userQueryPort, UserMapper mapper, JwtTokenProvider jwtTokenProvider) {
        this.userQueryPort = userQueryPort;
        this.mapper = mapper;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> execute(Authentication authentication) {
        log.info("Listando usuários...");
        String token = (String) authentication.getCredentials();
        String roleFromToken = jwtTokenProvider.getRoleFromToken(token);
        
        log.debug("Filtrando usuários por role: {}", roleFromToken);

        List<User> users = roleFromToken.equals(Roles.ALUNO.getRole())
            ? userQueryPort.findByRole(Roles.PERSONAL.getRole())
            : userQueryPort.findAll();
        
        log.debug("Total de usuários retornados: {}", users.size());

        return users.stream()
                .map(mapper::toResponse)
                .toList();
    }
}