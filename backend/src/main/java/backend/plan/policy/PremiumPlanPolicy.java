package backend.plan.policy;

import org.springframework.stereotype.Component;

@Component
public class PremiumPlanPolicy implements PlanPolicy {

    @Override
    public int getMaxStudents() {
        return 999;
    }

    @Override
    public int getMaxPrograms() {
        return 99;
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
        return "PREMIUM";
    }
}

