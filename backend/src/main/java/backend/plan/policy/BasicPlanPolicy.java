package backend.plan.policy;

import org.springframework.stereotype.Component;

@Component
public class BasicPlanPolicy implements PlanPolicy {

    @Override
    public int getMaxStudents() {
        return 20;
    }

    @Override
    public int getMaxPrograms() {
        return 3;
    }

    @Override
    public boolean allowsCustomExercises() {
        return true;
    }

    @Override
    public boolean allowsVideoUrl() {
        return false;
    }

    @Override
    public String getPolicyKey() {
        return "BASIC";
    }
}

