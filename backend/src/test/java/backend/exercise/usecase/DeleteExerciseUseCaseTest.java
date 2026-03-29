package backend.exercise.usecase;

import backend.exercise.model.entity.Exercise;
import backend.exercise.port.ExerciseCommandPort;
import backend.exercise.port.ExerciseQueryPort;
import backend.exercise.port.ExerciseUsagePort;
import backend.infrastructure.exception.BadRequestException;
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
class DeleteExerciseUseCaseTest {

    @Mock
    private ExerciseQueryPort queryPort;

    @Mock
    private ExerciseCommandPort commandPort;

    @Mock
    private ExerciseUsagePort exerciseUsagePort;

    @InjectMocks
    private DeleteExerciseUseCase useCase;

    @Test
    void executeShouldDeleteExerciseWhenNotInUse() {
        Exercise exercise = Exercise.builder().id(3L).name("Supino").build();

        when(queryPort.findByIdWithCategory(3L)).thenReturn(Optional.of(exercise));
        when(exerciseUsagePort.isExerciseInUse(3L)).thenReturn(false);

        useCase.execute(3L);

        verify(commandPort).deleteById(3L);
    }

    @Test
    void executeShouldThrowWhenExerciseIsInUse() {
        Exercise exercise = Exercise.builder().id(3L).name("Supino").build();

        when(queryPort.findByIdWithCategory(3L)).thenReturn(Optional.of(exercise));
        when(exerciseUsagePort.isExerciseInUse(3L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(3L));

        assertEquals("Não é possível deletar exercício em uso", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenExerciseIsNotFound() {
        when(queryPort.findByIdWithCategory(99L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(99L));

        assertEquals("Exercício não encontrado", ex.getMessage());
        verify(commandPort, never()).deleteById(99L);
    }
}

