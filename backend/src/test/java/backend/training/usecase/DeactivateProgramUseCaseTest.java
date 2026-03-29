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
class DeactivateProgramUseCaseTest {

    @Mock
    private TrainingProgramQueryPort queryPort;

    @Mock
    private TrainingProgramCommandPort commandPort;

    @InjectMocks
    private DeactivateProgramUseCase useCase;

    @Test
    void executeShouldDeactivateProgramWhenActive() {
        TrainingProgram program = TrainingProgram.builder().id(1L).name("Programa").active(true).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(program));

        assertTrue(program.getActive());
        useCase.execute(1L);

        assertFalse(program.getActive());
        verify(commandPort).update(program);
    }

    @Test
    void executeShouldThrowWhenProgramIsAlreadyInactive() {
        TrainingProgram program = TrainingProgram.builder().id(1L).active(false).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(program));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Programa já está inativo", ex.getMessage());
    }
}

