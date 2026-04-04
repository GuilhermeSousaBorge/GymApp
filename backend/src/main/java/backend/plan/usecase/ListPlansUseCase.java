package backend.plan.usecase;

import backend.plan.dto.PlanResponse;
import backend.plan.mapper.PlanMapper;
import backend.plan.port.PlanQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListPlansUseCase {

    private final PlanQueryPort queryPort;
    private final PlanMapper mapper;

    @Transactional(readOnly = true)
    public List<PlanResponse> execute(Boolean activeOnly) {
        log.info("Listando planos. activeOnly={}", activeOnly);

        if (Boolean.TRUE.equals(activeOnly)) {
            return queryPort.findAllActive().stream().map(mapper::toResponse).toList();
        }

        return queryPort.findAll().stream().map(mapper::toResponse).toList();
    }
}

