package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.dto.PlanResponse;
import backend.plan.dto.PlanUpdateRequest;
import backend.plan.mapper.PlanMapper;
import backend.plan.model.entity.Plan;
import backend.plan.model.valueObject.Money;
import backend.plan.port.PlanCommandPort;
import backend.plan.port.PlanQueryPort;
import backend.plan.port.PlanValidationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdatePlanUseCase {

    private final PlanQueryPort queryPort;
    private final PlanCommandPort commandPort;
    private final PlanValidationPort validationPort;
    private final PlanMapper mapper;

    @Transactional
    public PlanResponse execute(Long id, PlanUpdateRequest request) {
        log.info("Atualizando plano com ID: {}", id);

        Plan plan = queryPort.findById(id)
                .orElseThrow(() -> new BadRequestException("Plano nao encontrado"));

        if (request.getName() != null
                && !request.getName().equalsIgnoreCase(plan.getName())
                && validationPort.existsByNameAndIdNot(request.getName(), id)) {
            throw new BadRequestException("Plano com este nome ja existe");
        }

        if (request.getName() != null) {
            plan.setName(request.getName());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            plan.setPrice(new Money(request.getPrice()));
        }
        if (request.getMaxStudents() != null) {
            plan.setMaxStudents(request.getMaxStudents());
        }
        if (request.getMaxPrograms() != null) {
            plan.setMaxPrograms(request.getMaxPrograms());
        }
        if (request.getBenefits() != null) {
            plan.setBenefits(new HashSet<>(request.getBenefits()));
        }
        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }

        Plan updated = commandPort.update(plan);
        return mapper.toResponse(updated);
    }
}

