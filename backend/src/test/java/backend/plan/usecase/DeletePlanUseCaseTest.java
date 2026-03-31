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
class DeletePlanUseCaseTest {

    @Mock
    private PlanQueryPort queryPort;

    @Mock
    private PlanCommandPort commandPort;

    @InjectMocks
    private DeletePlanUseCase useCase;

    @Test
    void executeShouldDeleteWhenPlanExists() {
        when(queryPort.findById(10L)).thenReturn(Optional.of(Plan.builder().id(10L).build()));

        useCase.execute(10L);

        verify(commandPort).deleteById(10L);
    }

    @Test
    void executeShouldThrowWhenPlanNotFound() {
        when(queryPort.findById(10L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(10L));

        assertEquals("Plano nao encontrado", ex.getMessage());
    }
}
