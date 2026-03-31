package backend.subscription.dto;

import backend.subscription.model.enums.SubscriptionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private Long id;
    private Long planId;
    private String planName;
    private Long userId;
    private String userName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private SubscriptionStatus status;
    private LocalDateTime cancelledAt;
    private Boolean autoRenew;
    private BigDecimal planPriceAtStart;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

