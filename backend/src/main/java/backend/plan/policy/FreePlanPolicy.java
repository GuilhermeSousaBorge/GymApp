package backend.plan.policy;

import org.springframework.stereotype.Component;

@Component
public class FreePlanPolicy implements PlanPolicy {

    @Override
    public int getMaxStudents() {
        return 5;
    }

    @Override
    public int getMaxPrograms() {
        return 1;
    }

    @Override
    public boolean allowsCustomExercises() {
        return false;
    }

    @Override
    public boolean allowsVideoUrl() {
        return false;
    }

    @Override
    public String getPolicyKey() {
        return "FREE";
    }
}

