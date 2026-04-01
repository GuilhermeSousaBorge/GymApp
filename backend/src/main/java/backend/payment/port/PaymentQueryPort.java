package backend.payment.port;

import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentQueryPort {

    Optional<Payment> findById(Long id);

    List<Payment> findBySubscriptionId(Long subscriptionId);

    long countByStatus(PaymentStatus status);

    BigDecimal sumAmountByStatusAndPaymentDateBetween(PaymentStatus status, LocalDate start, LocalDate end);
}

