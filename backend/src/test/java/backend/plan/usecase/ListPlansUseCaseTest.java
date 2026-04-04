package backend.plan.usecase;

import backend.plan.dto.PlanResponse;
import backend.plan.mapper.PlanMapper;
import backend.plan.model.entity.Plan;
import backend.plan.port.PlanQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPlansUseCaseTest {

    @Mock
    private PlanQueryPort queryPort;

    @Mock
    private PlanMapper mapper;

    @InjectMocks
    private ListPlansUseCase useCase;

    @Test
    void executeShouldListOnlyActiveWhenRequested() {
        Plan active = Plan.builder().id(1L).name("Free").active(true).build();
        PlanResponse response = PlanResponse.builder().id(1L).name("Free").build();

        when(queryPort.findAllActive()).thenReturn(List.of(active));
        when(mapper.toResponse(active)).thenReturn(response);

        List<PlanResponse> result = useCase.execute(true);

        assertEquals(1, result.size());
        assertEquals("Free", result.get(0).getName());
    }

    @Test
    void executeShouldListAllWhenActiveOnlyIsFalse() {
        Plan p1 = Plan.builder().id(1L).name("Free").build();
        Plan p2 = Plan.builder().id(2L).name("Basic").build();

        when(queryPort.findAll()).thenReturn(List.of(p1, p2));
        when(mapper.toResponse(p1)).thenReturn(PlanResponse.builder().id(1L).name("Free").build());
        when(mapper.toResponse(p2)).thenReturn(PlanResponse.builder().id(2L).name("Basic").build());

        List<PlanResponse> result = useCase.execute(false);

        assertEquals(2, result.size());
    }
}
