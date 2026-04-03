package backend.payment.usecase;

import backend.payment.dto.PaymentResponse;
import backend.payment.mapper.PaymentMapper;
import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListPaymentsByStatusUseCase {

    private final PaymentQueryPort queryPort;
    private final PaymentMapper mapper;

    @Transactional(readOnly = true)
    public List<PaymentResponse> execute(PaymentStatus status) {
        log.info("Listando pagamentos com status {}", status);
        List<Payment> payments = status  != null ? queryPort.findByStatus(status) : queryPort.findAll();
        return payments.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
