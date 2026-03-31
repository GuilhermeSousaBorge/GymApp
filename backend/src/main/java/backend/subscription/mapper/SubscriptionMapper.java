package backend.subscription.mapper;

import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.model.entity.Subscription;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionMapper {

    public SubscriptionResponse toResponse(Subscription subscription) {
        if (subscription == null) {
            return null;
        }

        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .planId(subscription.getPlan() != null ? subscription.getPlan().getId() : null)
                .planName(subscription.getPlan() != null ? subscription.getPlan().getName() : null)
                .userId(subscription.getUser() != null ? subscription.getUser().getId() : null)
                .userName(subscription.getUser() != null ? subscription.getUser().getName() : null)
                .startDate(subscription.getStartDate())
                .endDate(subscription.getEndDate())
                .status(subscription.getStatus())
                .cancelledAt(subscription.getCancelledAt())
                .autoRenew(subscription.getAutoRenew())
                .planPriceAtStart(subscription.getPlanPriceAtStart())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
}

