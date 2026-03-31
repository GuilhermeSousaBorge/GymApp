package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.port.PlanCommandPort;
import backend.plan.port.PlanQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeletePlanUseCase {

    private final PlanQueryPort queryPort;
    private final PlanCommandPort commandPort;

    @Transactional
    public void execute(Long id) {
        log.info("Deletando plano com ID: {}", id);

        if (queryPort.findById(id).isEmpty()) {
            throw new BadRequestException("Plano nao encontrado");
        }

        commandPort.deleteById(id);
    }
}

