package backend.plan.policy;

import backend.plan.model.entity.Plan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PlanPolicyResolver {

    private final List<PlanPolicy> policies;

    public PlanPolicy resolve(Plan plan) {
        String key = normalizePlanName(plan.getName());

        return policies.stream()
                .filter(policy -> policy.getPolicyKey().equalsIgnoreCase(key))
                .findFirst()
                .orElseGet(() -> fromPlan(plan));
    }

    private String normalizePlanName(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase();
    }

    private PlanPolicy fromPlan(Plan plan) {
        return new PlanPolicy() {
            @Override
            public int getMaxStudents() {
                return plan.getMaxStudents();
            }

            @Override
            public int getMaxPrograms() {
                return plan.getMaxPrograms();
            }

            @Override
            public boolean allowsCustomExercises() {
                return true;
            }

            @Override
            public boolean allowsVideoUrl() {
                return true;
            }

            @Override
            public String getPolicyKey() {
                return normalizePlanName(plan.getName());
            }
        };
    }
}

