package backend.subscription.adapter;

import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionCommandPort;
import backend.subscription.port.SubscriptionQueryPort;
import backend.subscription.port.SubscriptionValidationPort;
import backend.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionRepositoryAdapter
        implements SubscriptionQueryPort, SubscriptionCommandPort, SubscriptionValidationPort {

    private final SubscriptionRepository repository;

    @Override
    public Optional<Subscription> findById(Long id) {
        log.debug("SubscriptionRepositoryAdapter: findById({})", id);
        return repository.findById(id);
    }

    @Override
    public Optional<Subscription> findActiveByUserId(Long userId) {
        log.debug("SubscriptionRepositoryAdapter: findActiveByUserId({})", userId);
        return repository.findFirstByUserIdAndStatusOrderByStartDateDesc(userId, SubscriptionStatus.ACTIVE);
    }

    @Override
    public List<Subscription> findByUserId(Long userId) {
        log.debug("SubscriptionRepositoryAdapter: findByUserId({})", userId);
        return repository.findByUserIdOrderByStartDateDesc(userId);
    }

    @Override
    public Subscription save(Subscription subscription) {
        log.debug("SubscriptionRepositoryAdapter: save({})", subscription.getId());
        return repository.save(subscription);
    }

    @Override
    public Subscription update(Subscription subscription) {
        log.debug("SubscriptionRepositoryAdapter: update({})", subscription.getId());
        return repository.save(subscription);
    }

    @Override
    public boolean existsActiveByUserId(Long userId) {
        log.debug("SubscriptionRepositoryAdapter: existsActiveByUserId({})", userId);
        return repository.existsByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE);
    }

    @Override
    public long countByStatus(SubscriptionStatus status) {
        log.debug("SubscriptionRepositoryAdapter: countByStatus({})", status);
        return repository.countByStatus(status);
    }
}

