package backend.plan.policy;

public interface PlanPolicy {

    int getMaxStudents();

    int getMaxPrograms();

    boolean allowsCustomExercises();

    boolean allowsVideoUrl();

    String getPolicyKey();
}

