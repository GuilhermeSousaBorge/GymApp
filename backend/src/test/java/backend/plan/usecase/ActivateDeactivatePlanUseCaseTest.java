package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.model.entity.Plan;
import backend.plan.port.PlanCommandPort;
import backend.plan.port.PlanQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivateDeactivatePlanUseCaseTest {

    @Mock
    private PlanQueryPort queryPort;

    @Mock
    private PlanCommandPort commandPort;

    @InjectMocks
    private ActivatePlanUseCase activateUseCase;

    @InjectMocks
    private DeactivatePlanUseCase deactivateUseCase;

    @Test
    void activateShouldSetActiveTrue() {
        Plan plan = Plan.builder().id(1L).active(false).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(plan));

        activateUseCase.execute(1L);

        assertEquals(true, plan.getActive());
        verify(commandPort).update(plan);
    }

    @Test
    void deactivateShouldThrowWhenPlanNotFound() {
        when(queryPort.findById(99L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> deactivateUseCase.execute(99L));

        assertEquals("Plano nao encontrado", ex.getMessage());
    }
}
