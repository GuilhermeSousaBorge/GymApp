package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivateSheetUseCaseTest {

    @Mock
    private TrainingSheetQueryPort queryPort;

    @Mock
    private TrainingSheetCommandPort commandPort;

    @InjectMocks
    private ActivateSheetUseCase useCase;

    @Test
    void executeShouldActivateSheetWhenInactive() {
        TrainingSheet sheet = TrainingSheet.builder().id(1L).name("Ficha").active(false).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(sheet));

        assertFalse(sheet.getActive());
        useCase.execute(1L);

        assertTrue(sheet.getActive());
        verify(commandPort).update(sheet);
    }

    @Test
    void executeShouldThrowWhenSheetIsAlreadyActive() {
        TrainingSheet sheet = TrainingSheet.builder().id(1L).active(true).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(sheet));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Folha já está ativa", ex.getMessage());
    }
}

