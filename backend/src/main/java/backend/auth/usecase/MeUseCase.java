package backend.auth.usecase;

import backend.infrastructure.exception.UnauthorizedException;
import backend.user.dto.UserResponse;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MeUseCase {

    private final UserQueryPort userQueryPort;
    private final UserMapper mapper;

    @Transactional(readOnly = true)
    public UserResponse execute(Authentication authentication){
        log.info("Buscando usuario autenticado");

        var userId = authentication.getPrincipal();
        if(userId instanceof Long) {
            User user = userQueryPort.findById((Long) userId)
                    .orElseThrow(() -> new UnauthorizedException("Usuário não encontrado"));
            return mapper.toResponse(user);
        }
        throw  new UnauthorizedException("Usuário não autenticado");
    }
}
