package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.model.entity.Plan;
import backend.plan.port.PlanCommandPort;
import backend.plan.port.PlanQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeactivatePlanUseCase {

    private final PlanQueryPort queryPort;
    private final PlanCommandPort commandPort;

    @Transactional
    public void execute(Long id) {
        log.info("Desativando plano com ID: {}", id);

        Plan plan = queryPort.findById(id)
                .orElseThrow(() -> new BadRequestException("Plano nao encontrado"));

        plan.setActive(false);
        commandPort.update(plan);
    }
}

