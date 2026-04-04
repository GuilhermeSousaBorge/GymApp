package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.dto.PlanResponse;
import backend.plan.mapper.PlanMapper;
import backend.plan.model.entity.Plan;
import backend.plan.port.PlanQueryPort;
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
class GetPlanByIdUseCaseTest {

    @Mock
    private PlanQueryPort queryPort;

    @Mock
    private PlanMapper mapper;

    @InjectMocks
    private GetPlanByIdUseCase useCase;

    @Test
    void executeShouldReturnPlanWhenFound() {
        Plan plan = Plan.builder().id(1L).name("Free").build();
        PlanResponse response = PlanResponse.builder().id(1L).name("Free").build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(plan));
        when(mapper.toResponse(plan)).thenReturn(response);

        PlanResponse result = useCase.execute(1L);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenPlanNotFound() {
        when(queryPort.findById(99L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(99L));

        assertEquals("Plano nao encontrado", ex.getMessage());
    }
}

