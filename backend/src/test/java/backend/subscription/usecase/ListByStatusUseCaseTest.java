package backend.subscription.usecase;

import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.mapper.SubscriptionMapper;
import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionQueryPort;
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
class ListByStatusUseCaseTest {

    @Mock
    private SubscriptionQueryPort queryPort;

    @Mock
    private SubscriptionMapper mapper;

    @InjectMocks
    private ListByStatusUseCase useCase;

    @Test
    void executeShouldListByStatusWhenStatusIsProvided() {
        Subscription subscription = Subscription.builder().id(1L).status(SubscriptionStatus.ACTIVE).build();
        SubscriptionResponse response = SubscriptionResponse.builder().id(1L).status(SubscriptionStatus.ACTIVE).build();

        when(queryPort.findByStatus(SubscriptionStatus.ACTIVE)).thenReturn(List.of(subscription));
        when(mapper.toResponse(subscription)).thenReturn(response);

        List<SubscriptionResponse> result = useCase.execute(SubscriptionStatus.ACTIVE);

        assertEquals(1, result.size());
        assertEquals(SubscriptionStatus.ACTIVE, result.get(0).getStatus());
        verify(queryPort).findByStatus(SubscriptionStatus.ACTIVE);
    }

    @Test
    void executeShouldListAllWhenStatusIsNull() {
        Subscription subscription = Subscription.builder().id(2L).status(SubscriptionStatus.PAST_DUE).build();
        SubscriptionResponse response = SubscriptionResponse.builder().id(2L).status(SubscriptionStatus.PAST_DUE).build();

        when(queryPort.findAll()).thenReturn(List.of(subscription));
        when(mapper.toResponse(subscription)).thenReturn(response);

        List<SubscriptionResponse> result = useCase.execute(null);

        assertEquals(1, result.size());
        assertEquals(SubscriptionStatus.PAST_DUE, result.get(0).getStatus());
        verify(queryPort).findAll();
    }
}

