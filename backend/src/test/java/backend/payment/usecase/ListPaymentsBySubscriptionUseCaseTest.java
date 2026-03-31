package backend.payment.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.payment.dto.PaymentResponse;
import backend.payment.mapper.PaymentMapper;
import backend.payment.model.entity.Payment;
import backend.payment.port.PaymentQueryPort;
import backend.subscription.model.entity.Subscription;
import backend.subscription.port.SubscriptionQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPaymentsBySubscriptionUseCaseTest {

    @Mock
    private SubscriptionQueryPort subscriptionQueryPort;

    @Mock
    private PaymentQueryPort queryPort;

    @Mock
    private PaymentMapper mapper;

    @InjectMocks
    private ListPaymentsBySubscriptionUseCase useCase;

    @Test
    void executeShouldListPaymentsWhenSubscriptionExists() {
        Payment payment = Payment.builder().id(1L).build();
        PaymentResponse response = PaymentResponse.builder().id(1L).build();

        when(subscriptionQueryPort.findById(1L)).thenReturn(Optional.of(Subscription.builder().id(1L).build()));
        when(queryPort.findBySubscriptionId(1L)).thenReturn(List.of(payment));
        when(mapper.toResponse(payment)).thenReturn(response);

        List<PaymentResponse> result = useCase.execute(1L);

        assertEquals(1, result.size());
    }

    @Test
    void executeShouldThrowWhenSubscriptionNotFound() {
        when(subscriptionQueryPort.findById(99L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(99L));

        assertEquals("Assinatura nao encontrada", ex.getMessage());
    }
}
