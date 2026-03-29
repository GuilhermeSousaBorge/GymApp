package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramCommandPort;
import backend.training.port.TrainingProgramQueryPort;
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
class ActivateProgramUseCaseTest {

    @Mock
    private TrainingProgramQueryPort queryPort;

    @Mock
    private TrainingProgramCommandPort commandPort;

    @InjectMocks
    private ActivateProgramUseCase useCase;

    @Test
    void executeShouldActivateProgramWhenInactive() {
        TrainingProgram program = TrainingProgram.builder().id(1L).name("Programa").active(false).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(program));

        assertFalse(program.getActive());
        useCase.execute(1L);

        assertTrue(program.getActive());
        verify(commandPort).update(program);
    }

    @Test
    void executeShouldThrowWhenProgramIsAlreadyActive() {
        TrainingProgram program = TrainingProgram.builder().id(1L).active(true).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(program));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Programa já está ativo", ex.getMessage());
    }
}

