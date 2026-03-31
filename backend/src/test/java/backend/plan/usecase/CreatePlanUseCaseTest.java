package backend.plan.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.dto.PlanRequest;
import backend.plan.dto.PlanResponse;
import backend.plan.mapper.PlanMapper;
import backend.plan.model.entity.Plan;
import backend.plan.model.valueObject.Money;
import backend.plan.port.PlanCommandPort;
import backend.plan.port.PlanValidationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreatePlanUseCaseTest {

    @Mock
    private PlanCommandPort commandPort;

    @Mock
    private PlanValidationPort validationPort;

    @Mock
    private PlanMapper mapper;

    @InjectMocks
    private CreatePlanUseCase useCase;

    @Test
    void executeShouldCreatePlanWhenNameIsAvailable() {
        PlanRequest request = PlanRequest.builder()
                .name("Starter")
                .description("Plano inicial")
                .price(new BigDecimal("29.90"))
                .maxStudents(10)
                .maxPrograms(2)
                .build();

        Plan saved = Plan.builder().id(1L).name("Starter").price(new Money(new BigDecimal("29.90"))).maxStudents(10).maxPrograms(2).build();
        PlanResponse response = PlanResponse.builder().id(1L).name("Starter").build();

        when(validationPort.existsByName("Starter")).thenReturn(false);
        when(commandPort.save(any(Plan.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        PlanResponse result = useCase.execute(request);

        assertSame(response, result);
        ArgumentCaptor<Plan> captor = ArgumentCaptor.forClass(Plan.class);
        verify(commandPort).save(captor.capture());
        assertEquals("Starter", captor.getValue().getName());
    }

    @Test
    void executeShouldThrowWhenNameAlreadyExists() {
        PlanRequest request = PlanRequest.builder().name("Starter").build();
        when(validationPort.existsByName("Starter")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Plano com este nome ja existe", ex.getMessage());
    }
}

