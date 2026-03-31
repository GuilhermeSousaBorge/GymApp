package backend.payment.port;

import backend.payment.model.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentQueryPort {

    Optional<Payment> findById(Long id);

    List<Payment> findBySubscriptionId(Long subscriptionId);
}

