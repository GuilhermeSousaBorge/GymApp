package backend.payment.adapter;

import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentCommandPort;
import backend.payment.port.PaymentQueryPort;
import backend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRepositoryAdapter implements PaymentQueryPort, PaymentCommandPort {

    private final PaymentRepository repository;

    @Override
    public Optional<Payment> findById(Long id) {
        log.debug("PaymentRepositoryAdapter: findById({})", id);
        return repository.findById(id);
    }

    @Override
    public List<Payment> findBySubscriptionId(Long subscriptionId) {
        log.debug("PaymentRepositoryAdapter: findBySubscriptionId({})", subscriptionId);
        return repository.findBySubscriptionIdOrderByDueDateDesc(subscriptionId);
    }

    @Override
    public long countByStatus(PaymentStatus status) {
        log.debug("PaymentRepositoryAdapter: countByStatus({})", status);
        return repository.countByStatus(status);
    }

    @Override
    public BigDecimal sumAmountByStatusAndPaymentDateBetween(PaymentStatus status, LocalDate start, LocalDate end) {
        log.debug("PaymentRepositoryAdapter: sumAmountByStatusAndPaymentDateBetween({}, {}, {})", status, start, end);
        return repository.sumAmountByStatusAndPaymentDateBetween(status, start, end);
    }

    @Override
    public Payment save(Payment payment) {
        log.debug("PaymentRepositoryAdapter: save({})", payment.getId());
        return repository.save(payment);
    }

    @Override
    public Payment update(Payment payment) {
        log.debug("PaymentRepositoryAdapter: update({})", payment.getId());
        return repository.save(payment);
    }
}

