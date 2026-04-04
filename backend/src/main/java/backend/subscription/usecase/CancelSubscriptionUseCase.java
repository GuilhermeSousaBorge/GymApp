package backend.subscription.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionCommandPort;
import backend.subscription.port.SubscriptionQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CancelSubscriptionUseCase {

    private final SubscriptionQueryPort queryPort;
    private final SubscriptionCommandPort commandPort;

    @Transactional
    public void execute(Long subscriptionId) {
        log.info("Cancelando assinatura com ID: {}", subscriptionId);

        Subscription subscription = queryPort.findById(subscriptionId)
                .orElseThrow(() -> new BadRequestException("Assinatura nao encontrada"));

        if (SubscriptionStatus.CANCELLED.equals(subscription.getStatus())) {
            throw new BadRequestException("Assinatura ja esta cancelada");
        }

        if (SubscriptionStatus.EXPIRED.equals(subscription.getStatus())) {
            throw new BadRequestException("Transicao invalida: assinatura EXPIRED nao pode ser cancelada");
        }

        SubscriptionStatus previousStatus = subscription.getStatus();

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setCancelledAt(LocalDateTime.now());
        subscription.setAutoRenew(false);
        if (subscription.getEndDate() == null) {
            subscription.setEndDate(LocalDateTime.now());
        }

        commandPort.update(subscription);
        log.info("Assinatura {} atualizada de {} para {}", subscriptionId, previousStatus, subscription.getStatus());
    }
}

