package backend.plan.port;

import backend.plan.model.entity.Plan;

public interface PlanCommandPort {

    Plan save(Plan plan);

    Plan update(Plan plan);

    void deleteById(Long id);
}

