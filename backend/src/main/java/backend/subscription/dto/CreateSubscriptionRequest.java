package backend.subscription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotNull(message = "O plano e obrigatorio.")
    private Long planId;

    @NotNull(message = "O usuario e obrigatorio.")
    private Long userId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder.Default
    private Boolean autoRenew = true;
}

