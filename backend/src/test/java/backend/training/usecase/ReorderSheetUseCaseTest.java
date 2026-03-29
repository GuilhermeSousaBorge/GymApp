package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingSheetCommandPort;
import backend.training.port.TrainingSheetQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReorderSheetUseCaseTest {

    @Mock
    private TrainingSheetQueryPort queryPort;

    @Mock
    private TrainingSheetCommandPort commandPort;

    @Mock
    private TrainingSheetMapper mapper;

    @InjectMocks
    private ReorderSheetUseCase useCase;

    @Test
    void executeShouldUpdateOrderWhenNewOrderIsValid() {
        TrainingSheet sheet = TrainingSheet.builder().id(3L).name("Treino A").orderInProgram(1).build();
        TrainingSheetResponse response = TrainingSheetResponse.builder().id(3L).orderInProgram(2).build();

        when(queryPort.findById(3L)).thenReturn(Optional.of(sheet));
        when(commandPort.update(sheet)).thenReturn(sheet);
        when(mapper.toResponse(sheet)).thenReturn(response);

        TrainingSheetResponse result = useCase.execute(3L, 2);

        assertSame(response, result);
        assertEquals(2, sheet.getOrderInProgram());
        verify(commandPort).update(sheet);
    }

    @Test
    void executeShouldThrowWhenNewOrderIsInvalid() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(3L, 0));

        assertEquals("Ordem deve ser maior que 0", ex.getMessage());
    }
}

