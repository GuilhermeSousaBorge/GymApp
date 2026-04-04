package backend.subscription.port;

public interface SubscriptionValidationPort {

    boolean existsActiveByUserId(Long userId);
}

