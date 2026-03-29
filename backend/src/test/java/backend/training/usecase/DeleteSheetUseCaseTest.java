package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.port.TrainingSheetCommandPort;
import backend.training.port.TrainingSheetQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteSheetUseCaseTest {

    @Mock
    private TrainingSheetQueryPort sheetQueryPort;

    @Mock
    private TrainingSheetCommandPort sheetCommandPort;

    @Mock
    private TrainingExerciseQueryPort exerciseQueryPort;

    @InjectMocks
    private DeleteSheetUseCase useCase;

    @Test
    void executeShouldDeleteSheetWhenItHasNoExercises() {
        TrainingSheet sheet = TrainingSheet.builder().id(9L).name("Treino B").build();

        when(sheetQueryPort.findById(9L)).thenReturn(Optional.of(sheet));
        when(exerciseQueryPort.existsBySheet(9L)).thenReturn(false);

        useCase.execute(9L);

        verify(sheetCommandPort).deleteById(9L);
    }

    @Test
    void executeShouldThrowWhenSheetHasExercises() {
        TrainingSheet sheet = TrainingSheet.builder().id(9L).name("Treino B").build();

        when(sheetQueryPort.findById(9L)).thenReturn(Optional.of(sheet));
        when(exerciseQueryPort.existsBySheet(9L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(9L));

        assertEquals("Não é possível deletar folha com exercícios", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenSheetDoesNotExist() {
        when(sheetQueryPort.findById(404L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(404L));

        assertEquals("Ficha nao encontrada", ex.getMessage());
        verify(sheetCommandPort, never()).deleteById(404L);
    }
}

