package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.model.entity.TrainingExercise;
import backend.training.port.TrainingExerciseCommandPort;
import backend.training.port.TrainingExerciseQueryPort;
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
class DeleteExerciseUseCaseTest {

    @Mock
    private TrainingExerciseQueryPort queryPort;

    @Mock
    private TrainingExerciseCommandPort commandPort;

    @InjectMocks
    private DeleteExerciseUseCase useCase;

    @Test
    void executeShouldDeleteTrainingExerciseWhenFound() {
        TrainingExercise exercise = TrainingExercise.builder().id(1L).build();
        when(queryPort.findById(1L)).thenReturn(Optional.of(exercise));

        useCase.execute(1L);

        verify(commandPort).deleteById(1L);
    }

    @Test
    void executeShouldThrowWhenTrainingExerciseIsNotFound() {
        when(queryPort.findById(1L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Exercício de treinamento não encontrado", ex.getMessage());
    }
}

