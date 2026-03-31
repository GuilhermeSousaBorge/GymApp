package backend.subscription.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionCommandPort;
import backend.subscription.port.SubscriptionQueryPort;
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
class CancelSubscriptionUseCaseTest {

    @Mock
    private SubscriptionQueryPort queryPort;

    @Mock
    private SubscriptionCommandPort commandPort;

    @InjectMocks
    private CancelSubscriptionUseCase useCase;

    @Test
    void executeShouldCancelSubscriptionWhenActive() {
        Subscription subscription = Subscription.builder().id(1L).status(SubscriptionStatus.ACTIVE).autoRenew(true).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(subscription));

        useCase.execute(1L);

        assertEquals(SubscriptionStatus.CANCELLED, subscription.getStatus());
        assertEquals(false, subscription.getAutoRenew());
        assertNotNull(subscription.getCancelledAt());
        verify(commandPort).update(subscription);
    }

    @Test
    void executeShouldThrowWhenAlreadyCancelled() {
        Subscription subscription = Subscription.builder().id(1L).status(SubscriptionStatus.CANCELLED).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(subscription));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Assinatura ja esta cancelada", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenStatusIsExpired() {
        Subscription subscription = Subscription.builder().id(1L).status(SubscriptionStatus.EXPIRED).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(subscription));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Transicao invalida: assinatura EXPIRED nao pode ser cancelada", ex.getMessage());
    }
}
