package backend.user.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.user.model.entity.User;
import backend.user.port.UserCommandPort;
import backend.user.port.UserQueryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ActivateUserUseCase {

    private final UserQueryPort queryPort;
    private final UserCommandPort commandPort;

    public ActivateUserUseCase(UserQueryPort queryPort, UserCommandPort commandPort) {
        this.queryPort = queryPort;
        this.commandPort = commandPort;
    }

    @Transactional
    public void execute(Long id){
        log.info("Ativando usuário com ID: {}", id);

        User user = queryPort.findById(id).orElseThrow(() -> new BadRequestException("Usuário não encontrado"));

        user.setActive(true);

        commandPort.update(user);
        
        log.info("Usuário com ID: {} ativado com sucesso", id);
    }
}
