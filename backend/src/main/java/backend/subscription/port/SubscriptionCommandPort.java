package backend.subscription.port;

import backend.subscription.model.entity.Subscription;

public interface SubscriptionCommandPort {

    Subscription save(Subscription subscription);

    Subscription update(Subscription subscription);
}

