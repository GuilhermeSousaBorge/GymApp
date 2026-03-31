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

        Subscription subscription = subscriptionQueryPort.findById(request.getSubscriptionId())
                .orElseThrow(() -> new BadRequestException("Assinatura nao encontrada"));

        if (SubscriptionStatus.CANCELLED.equals(subscription.getStatus()) || SubscriptionStatus.EXPIRED.equals(subscription.getStatus())) {
            throw new BadRequestException("Nao e permitido gerar pagamento para assinatura inativa");
        }

        Payment saved = commandPort.save(Payment.builder()
                .subscription(subscription)
                .status(request.getStatus() != null ? request.getStatus() : PaymentStatus.PENDING)
                .amount(request.getAmount())
                .dueDate(request.getDueDate())
                .paymentMethod(request.getPaymentMethod())
                .build());

        return mapper.toResponse(saved);
    }

}


