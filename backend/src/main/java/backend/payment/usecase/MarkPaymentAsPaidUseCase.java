package backend.payment.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentCommandPort;
import backend.payment.port.PaymentQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarkPaymentAsPaidUseCase {

    private final PaymentQueryPort queryPort;
    private final PaymentCommandPort commandPort;

    @Transactional
    public void execute(Long paymentId) {
        log.info("Marcando pagamento como pago. paymentId={}", paymentId);

        Payment payment = queryPort.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Pagamento nao encontrado"));

        if (PaymentStatus.PAID.equals(payment.getStatus())) {
            throw new BadRequestException("Pagamento ja esta marcado como pago");
        }

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDate.now());

        commandPort.update(payment);
    }

}

