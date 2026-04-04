package backend.payment.dto;

import backend.payment.model.enums.PaymentMethod;
import backend.payment.model.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotNull(message = "A assinatura e obrigatoria.")
    private Long subscriptionId;

    @NotNull(message = "O valor do pagamento e obrigatorio.")
    @DecimalMin(value = "0.00", inclusive = false, message = "O valor do pagamento deve ser maior que zero.")
    private BigDecimal amount;

    @NotNull(message = "A data de vencimento e obrigatoria.")
    private LocalDate dueDate;

    @NotNull(message = "O metodo de pagamento e obrigatorio.")
    private PaymentMethod paymentMethod;

    private PaymentStatus status;
}

