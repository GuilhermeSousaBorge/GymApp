package backend.plan.mapper;

import backend.plan.dto.PlanResponse;
import backend.plan.model.entity.Plan;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class PlanMapper {

    public PlanResponse toResponse(Plan plan) {
        if (plan == null) {
            return null;
        }

        return PlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice() != null ? plan.getPrice().getValue() : null)
                .maxStudents(plan.getMaxStudents())
                .maxPrograms(plan.getMaxPrograms())
                .benefits(plan.getBenefits() != null ? new HashSet<>(plan.getBenefits()) : new HashSet<>())
                .active(plan.getActive())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}

