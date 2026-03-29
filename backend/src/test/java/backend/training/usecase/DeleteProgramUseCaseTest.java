package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingProgramCommandPort;
import backend.training.port.TrainingProgramQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProgramUseCaseTest {

    @Mock
    private TrainingProgramQueryPort queryPort;

    @Mock
    private TrainingProgramCommandPort commandPort;

    @InjectMocks
    private DeleteProgramUseCase useCase;

    @Test
    void executeShouldDeleteProgramWhenItHasNoSheets() {
        TrainingProgram program = TrainingProgram.builder().id(1L).trainingSheets(List.of()).build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(program));

        useCase.execute(1L);

        verify(commandPort).deleteById(1L);
    }

    @Test
    void executeShouldThrowWhenProgramHasSheets() {
        TrainingProgram program = TrainingProgram.builder().id(1L).trainingSheets(List.of(TrainingSheet.builder().id(2L).build())).build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(program));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Não é possível deletar programa com folhas associadas", ex.getMessage());
    }

    @Test
    void executeShouldDeleteProgramWhenSheetsListIsNull() {
        TrainingProgram program = TrainingProgram.builder().id(1L).trainingSheets(null).build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(program));

        useCase.execute(1L);

        verify(commandPort).deleteById(1L);
    }

    @Test
    void executeShouldThrowWhenProgramDoesNotExist() {
        when(queryPort.findById(404L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(404L));

        assertEquals("Ficha nao encontrada", ex.getMessage());
        verify(commandPort, never()).deleteById(404L);
    }
}

