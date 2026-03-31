package backend.payment.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.payment.dto.PaymentResponse;
import backend.payment.mapper.PaymentMapper;
import backend.payment.port.PaymentQueryPort;
import backend.subscription.port.SubscriptionQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListPaymentsBySubscriptionUseCase {

    private final SubscriptionQueryPort subscriptionQueryPort;
    private final PaymentQueryPort queryPort;
    private final PaymentMapper mapper;

    @Transactional(readOnly = true)
    public List<PaymentResponse> execute(Long subscriptionId) {
        log.info("Listando pagamentos da assinatura {}", subscriptionId);

        if (subscriptionQueryPort.findById(subscriptionId).isEmpty()) {
            throw new BadRequestException("Assinatura nao encontrada");
        }

        return queryPort.findBySubscriptionId(subscriptionId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

}

