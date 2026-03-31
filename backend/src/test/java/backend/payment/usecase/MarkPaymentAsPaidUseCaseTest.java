package backend.payment.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentCommandPort;
import backend.payment.port.PaymentQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarkPaymentAsPaidUseCaseTest {

    @Mock
    private PaymentQueryPort queryPort;

    @Mock
    private PaymentCommandPort commandPort;

    @InjectMocks
    private MarkPaymentAsPaidUseCase useCase;

    @Test
    void executeShouldMarkAsPaidWhenPending() {
        Payment payment = Payment.builder().id(1L).status(PaymentStatus.PENDING).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(payment));

        useCase.execute(1L);

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertNotNull(payment.getPaymentDate());
        verify(commandPort).update(payment);
    }

    @Test
    void executeShouldThrowWhenAlreadyPaid() {
        Payment payment = Payment.builder().id(1L).status(PaymentStatus.PAID).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(payment));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Pagamento ja esta marcado como pago", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenPaymentNotFound() {
        when(queryPort.findById(99L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(99L));

        assertEquals("Pagamento nao encontrado", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenStatusTransitionIsInvalid() {
        Payment payment = Payment.builder().id(1L).status(PaymentStatus.CANCELLED).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(payment));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Transicao invalida: somente pagamento PENDING pode virar PAID", ex.getMessage());
    }
}
