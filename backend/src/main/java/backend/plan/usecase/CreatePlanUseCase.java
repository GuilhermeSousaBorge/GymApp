package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.dto.PlanRequest;
import backend.plan.dto.PlanResponse;
import backend.plan.mapper.PlanMapper;
import backend.plan.model.entity.Plan;
import backend.plan.model.valueObject.Money;
import backend.plan.port.PlanCommandPort;
import backend.plan.port.PlanValidationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatePlanUseCase {

    private final PlanCommandPort commandPort;
    private final PlanValidationPort validationPort;
    private final PlanMapper mapper;

    @Transactional
    public PlanResponse execute(PlanRequest request) {
        log.info("Criando plano: {}", request.getName());

        if (validationPort.existsByName(request.getName())) {
            throw new BadRequestException("Plano com este nome ja existe");
        }

        Plan saved = commandPort.save(Plan.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(new Money(request.getPrice()))
                .maxStudents(request.getMaxStudents())
                .maxPrograms(request.getMaxPrograms())
                .benefits(request.getBenefits() != null ? new HashSet<>(request.getBenefits()) : new HashSet<>())
                .active(true)
                .build());

        return mapper.toResponse(saved);
    }
}

