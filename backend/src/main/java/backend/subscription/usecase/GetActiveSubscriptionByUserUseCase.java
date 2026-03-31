package backend.subscription.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.mapper.SubscriptionMapper;
import backend.subscription.model.entity.Subscription;
import backend.subscription.port.SubscriptionQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetActiveSubscriptionByUserUseCase {

    private final SubscriptionQueryPort queryPort;
    private final SubscriptionMapper mapper;

    @Transactional(readOnly = true)
    public SubscriptionResponse execute(Long userId) {
        log.info("Buscando assinatura ativa do usuario {}", userId);

        Subscription subscription = queryPort.findActiveByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Usuario nao possui assinatura ativa"));

        return mapper.toResponse(subscription);
    }
}

