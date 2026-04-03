package backend.subscription.port;

import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;

import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryPort {

    Optional<Subscription> findById(Long id);

    Optional<Subscription> findActiveByUserId(Long userId);

    List<Subscription> findByUserId(Long userId);

    long countByStatus(SubscriptionStatus status);

    List<Subscription> findByStatus(SubscriptionStatus status);

    List<Subscription> findAll();
}

