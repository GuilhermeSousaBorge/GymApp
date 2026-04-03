package backend.payment.usecase;

import backend.payment.dto.PaymentResponse;
import backend.payment.mapper.PaymentMapper;
import backend.payment.model.entity.Payment;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPaymentsByStatusUseCaseTest {

    @Mock
    private PaymentQueryPort queryPort;

    @Mock
    private PaymentMapper mapper;

    @InjectMocks
    private ListPaymentsByStatusUseCase useCase;

    @Test
    void executeShouldListByStatusWhenStatusIsProvided() {
        Payment payment = Payment.builder().id(1L).status(PaymentStatus.PENDING).build();
        PaymentResponse response = PaymentResponse.builder().id(1L).status(PaymentStatus.PENDING).build();

        when(queryPort.findByStatus(PaymentStatus.PENDING)).thenReturn(List.of(payment));
        when(mapper.toResponse(payment)).thenReturn(response);

        List<PaymentResponse> result = useCase.execute(PaymentStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals(PaymentStatus.PENDING, result.get(0).getStatus());
        verify(queryPort).findByStatus(PaymentStatus.PENDING);
    }

    @Test
    void executeShouldListAllWhenStatusIsNull() {
        Payment payment = Payment.builder().id(2L).status(PaymentStatus.PAID).build();
        PaymentResponse response = PaymentResponse.builder().id(2L).status(PaymentStatus.PAID).build();

        when(queryPort.findAll()).thenReturn(List.of(payment));
        when(mapper.toResponse(payment)).thenReturn(response);

        List<PaymentResponse> result = useCase.execute(null);

        assertEquals(1, result.size());
        assertEquals(PaymentStatus.PAID, result.get(0).getStatus());
        verify(queryPort).findAll();
    }
}

