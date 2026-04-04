package backend.plan.port;

import backend.plan.model.entity.Plan;

import java.util.List;
import java.util.Optional;

public interface PlanQueryPort {

    Optional<Plan> findById(Long id);

    Optional<Plan> findByName(String name);

    List<Plan> findAll();

    List<Plan> findAllActive();
}

