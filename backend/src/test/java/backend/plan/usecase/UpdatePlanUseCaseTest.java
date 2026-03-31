package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.dto.PlanResponse;
import backend.plan.dto.PlanUpdateRequest;
import backend.plan.mapper.PlanMapper;
import backend.plan.model.entity.Plan;
import backend.plan.model.valueObject.Money;
import backend.plan.port.PlanCommandPort;
import backend.plan.port.PlanQueryPort;
import backend.plan.port.PlanValidationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdatePlanUseCaseTest {

    @Mock
    private PlanQueryPort queryPort;

    @Mock
    private PlanCommandPort commandPort;

    @Mock
    private PlanValidationPort validationPort;

    @Mock
    private PlanMapper mapper;

    @InjectMocks
    private UpdatePlanUseCase useCase;

    @Test
    void executeShouldUpdatePlanWhenValidRequest() {
        Plan plan = Plan.builder().id(1L).name("Free").price(new Money(new BigDecimal("0.00"))).maxStudents(5).maxPrograms(1).build();
        PlanUpdateRequest request = PlanUpdateRequest.builder().name("Free Plus").price(new BigDecimal("9.90")).build();
        PlanResponse response = PlanResponse.builder().id(1L).name("Free Plus").build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(plan));
        when(validationPort.existsByNameAndIdNot("Free Plus", 1L)).thenReturn(false);
        when(commandPort.update(any(Plan.class))).thenReturn(plan);
        when(mapper.toResponse(plan)).thenReturn(response);

        PlanResponse result = useCase.execute(1L, request);

        assertSame(response, result);
        assertEquals("Free Plus", plan.getName());
    }

    @Test
    void executeShouldThrowWhenNameAlreadyUsedByAnotherPlan() {
        Plan plan = Plan.builder().id(1L).name("Free").build();
        PlanUpdateRequest request = PlanUpdateRequest.builder().name("Basic").build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(plan));
        when(validationPort.existsByNameAndIdNot("Basic", 1L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L, request));

        assertEquals("Plano com este nome ja existe", ex.getMessage());
    }
}
