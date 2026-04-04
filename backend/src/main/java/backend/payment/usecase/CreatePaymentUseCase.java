package backend.payment.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.payment.dto.CreatePaymentRequest;
import backend.payment.dto.PaymentResponse;
import backend.payment.mapper.PaymentMapper;
import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentCommandPort;
import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreatePaymentUseCase {

    private final SubscriptionQueryPort subscriptionQueryPort;
    private final PaymentCommandPort commandPort;
    private final PaymentMapper mapper;

    @Transactional
    public PaymentResponse execute(CreatePaymentRequest request) {
        log.info("Criando pagamento para subscriptionId={}", request.getSubscriptionId());

        validateRequiredFields(request);

        Subscription subscription = subscriptionQueryPort.findById(request.getSubscriptionId())
                .orElseThrow(() -> new BadRequestException("Assinatura nao encontrada"));

        if (SubscriptionStatus.CANCELLED.equals(subscription.getStatus()) || SubscriptionStatus.EXPIRED.equals(subscription.getStatus())) {
            throw new BadRequestException("Nao e permitido gerar pagamento para assinatura inativa");
        }

        PaymentStatus initialStatus = request.getStatus() != null ? request.getStatus() : PaymentStatus.PENDING;
        if (!PaymentStatus.PENDING.equals(initialStatus)) {
            throw new BadRequestException("Transicao invalida: pagamento deve ser criado com status PENDING");
        }

        Payment saved = commandPort.save(Payment.builder()
                .subscription(subscription)
                .status(initialStatus)
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .paymentMethod(request.getPaymentMethod())
                .build());

        log.info("Pagamento {} criado com status {} para assinatura {}", saved.getId(), saved.getStatus(), request.getSubscriptionId());

        return mapper.toResponse(saved);
    }

    private void validateRequiredFields(CreatePaymentRequest request) {
        if (request.getSubscriptionId() == null) {
            throw new BadRequestException("subscriptionId e obrigatorio");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("amount deve ser maior que zero");
        }

        if (request.getDueDate() == null) {
            throw new BadRequestException("dueDate e obrigatorio");
        }

        if (request.getPaymentMethod() == null) {
            throw new BadRequestException("paymentMethod e obrigatorio");
        }
    }

}


