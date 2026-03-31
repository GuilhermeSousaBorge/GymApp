package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.dto.PlanResponse;
import backend.plan.mapper.PlanMapper;
import backend.plan.model.entity.Plan;
import backend.plan.port.PlanQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetPlanByIdUseCase {

    private final PlanQueryPort queryPort;
    private final PlanMapper mapper;

    @Transactional(readOnly = true)
    public PlanResponse execute(Long id) {
        log.info("Buscando plano com ID: {}", id);

        Plan plan = queryPort.findById(id)
                .orElseThrow(() -> new BadRequestException("Plano nao encontrado"));

        return mapper.toResponse(plan);
    }
}

