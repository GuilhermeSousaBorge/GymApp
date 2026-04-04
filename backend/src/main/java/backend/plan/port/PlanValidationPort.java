package backend.plan.port;

public interface PlanValidationPort {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}

