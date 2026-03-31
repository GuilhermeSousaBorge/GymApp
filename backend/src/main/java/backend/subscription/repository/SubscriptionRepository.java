package backend.subscription.repository;

import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findFirstByUserIdAndStatusOrderByStartDateDesc(Long userId, SubscriptionStatus status);

    boolean existsByUserIdAndStatus(Long userId, SubscriptionStatus status);

    List<Subscription> findByUserIdOrderByStartDateDesc(Long userId);
}

