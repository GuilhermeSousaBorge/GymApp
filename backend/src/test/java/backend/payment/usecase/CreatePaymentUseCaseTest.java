package backend.payment.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.payment.dto.CreatePaymentRequest;
import backend.payment.dto.PaymentResponse;
import backend.payment.mapper.PaymentMapper;
import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentMethod;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentCommandPort;
import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePaymentUseCaseTest {

    @Mock
    private SubscriptionQueryPort subscriptionQueryPort;

    @Mock
    private PaymentCommandPort commandPort;

    @Mock
    private PaymentMapper mapper;

    @InjectMocks
    private CreatePaymentUseCase useCase;

    @Test
    void executeShouldCreatePaymentWhenSubscriptionIsActive() {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .subscriptionId(1L)
                .amount(new BigDecimal("99.90"))
                .dueDate(LocalDate.now().plusDays(7))
                .paymentMethod(PaymentMethod.PIX)
                .build();

        Subscription subscription = Subscription.builder().id(1L).status(SubscriptionStatus.ACTIVE).build();
        Payment saved = Payment.builder().id(10L).subscription(subscription).status(PaymentStatus.PENDING).build();
        PaymentResponse response = PaymentResponse.builder().id(10L).build();

        when(subscriptionQueryPort.findById(1L)).thenReturn(Optional.of(subscription));
        when(commandPort.save(any(Payment.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        PaymentResponse result = useCase.execute(request);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenSubscriptionIsCancelled() {
        CreatePaymentRequest request = CreatePaymentRequest.builder()
                .subscriptionId(1L)
                .amount(new BigDecimal("99.90"))
                .dueDate(LocalDate.now().plusDays(7))
                .paymentMethod(PaymentMethod.PIX)
                .build();

        Subscription subscription = Subscription.builder().id(1L).status(SubscriptionStatus.CANCELLED).build();
        when(subscriptionQueryPort.findById(1L)).thenReturn(Optional.of(subscription));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Nao e permitido gerar pagamento para assinatura inativa", ex.getMessage());
    }
}
