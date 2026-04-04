package backend.user.usecase;


import backend.infrastructure.exception.BadRequestException;
import backend.user.dto.UserResponse;
import backend.user.mapper.UserMapper;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class GetUserByIdUseCase {

    private final UserQueryPort queryPort;
    private final UserMapper mapper;

    public GetUserByIdUseCase(UserQueryPort queryPort, UserMapper mapper) {
        this.queryPort =  queryPort;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public UserResponse execute(Long id){
        log.info("Buscando usuário por ID: {}", id);
        User user = queryPort.findById(id).orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        return mapper.toResponse(user);
    }
}