package backend.plan.adapter;

import backend.plan.model.entity.Plan;
import backend.plan.port.PlanCommandPort;
import backend.plan.port.PlanQueryPort;
import backend.plan.port.PlanValidationPort;
import backend.plan.repository.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanRepositoryAdapter implements PlanQueryPort, PlanCommandPort, PlanValidationPort {

    private final PlanRepository repository;

    @Override
    public Optional<Plan> findById(Long id) {
        log.debug("PlanRepositoryAdapter: findById({})", id);
        return repository.findById(id);
    }

    @Override
    public Optional<Plan> findByName(String name) {
        log.debug("PlanRepositoryAdapter: findByName({})", name);
        return repository.findByName(name);
    }

    @Override
    public List<Plan> findAll() {
        log.debug("PlanRepositoryAdapter: findAll()");
        return repository.findAll();
    }

    @Override
    public List<Plan> findAllActive() {
        log.debug("PlanRepositoryAdapter: findAllActive()");
        return repository.findByActiveTrue();
    }

    @Override
    public Plan save(Plan plan) {
        log.debug("PlanRepositoryAdapter: save({})", plan.getId());
        return repository.save(plan);
    }

    @Override
    public Plan update(Plan plan) {
        log.debug("PlanRepositoryAdapter: update({})", plan.getId());
        return repository.save(plan);
    }

    @Override
    public void deleteById(Long id) {
        log.debug("PlanRepositoryAdapter: deleteById({})", id);
        repository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("PlanRepositoryAdapter: existsByName({})", name);
        return repository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, Long id) {
        log.debug("PlanRepositoryAdapter: existsByNameAndIdNot({}, {})", name, id);
        return repository.existsByNameAndIdNot(name, id);
    }
}

