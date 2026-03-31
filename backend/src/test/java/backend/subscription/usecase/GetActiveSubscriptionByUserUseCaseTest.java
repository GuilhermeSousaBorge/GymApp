package backend.subscription.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.mapper.SubscriptionMapper;
import backend.subscription.model.entity.Subscription;
import backend.subscription.port.SubscriptionQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetActiveSubscriptionByUserUseCaseTest {

    @Mock
    private SubscriptionQueryPort queryPort;

    @Mock
    private SubscriptionMapper mapper;

    @InjectMocks
    private GetActiveSubscriptionByUserUseCase useCase;

    @Test
    void executeShouldReturnActiveSubscription() {
        Subscription subscription = Subscription.builder().id(10L).build();
        SubscriptionResponse response = SubscriptionResponse.builder().id(10L).build();

        when(queryPort.findActiveByUserId(2L)).thenReturn(Optional.of(subscription));
        when(mapper.toResponse(subscription)).thenReturn(response);

        SubscriptionResponse result = useCase.execute(2L);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenUserHasNoActiveSubscription() {
        when(queryPort.findActiveByUserId(2L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(2L));

        assertEquals("Usuario nao possui assinatura ativa", ex.getMessage());
    }
}
