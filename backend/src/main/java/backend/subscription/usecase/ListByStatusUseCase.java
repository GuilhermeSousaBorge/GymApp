package backend.subscription.usecase;

import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.mapper.SubscriptionMapper;
import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListByStatusUseCase {

    private final SubscriptionQueryPort queryPort;
    private final SubscriptionMapper mapper;

    @Transactional(readOnly = true)
    public List<SubscriptionResponse> execute(SubscriptionStatus status){
        log.info("Listando assinaturas com status: {}", status);
        List<Subscription> subscriptions = status != null ? queryPort.findByStatus(status) : queryPort.findAll();

        return subscriptions.stream()
                .map(mapper::toResponse)
                .toList();
    }
}
