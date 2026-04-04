package backend.payment.repository;

import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySubscriptionIdOrderByDueDateDesc(Long subscriptionId);

    long countByStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumAmountByStatusAndPaymentDateBetween(
            @Param("status") PaymentStatus status,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    List<Payment> findByStatus(PaymentStatus status);
}

