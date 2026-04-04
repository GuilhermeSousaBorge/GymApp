package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.dto.TrainingExerciseUpdateRequest;
import backend.training.mapper.TrainingExerciseMapper;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateExerciseUseCaseTest {

    @Mock
    private TrainingExerciseQueryPort queryPort;

    @Mock
    private TrainingExerciseCommandPort commandPort;

    @Mock
    private TrainingExerciseMapper mapper;

    @InjectMocks
    private UpdateExerciseUseCase useCase;

    @Test
    void executeShouldUpdateTrainingExerciseWhenFound() {
        TrainingExercise exercise = TrainingExercise.builder().id(1L).sets(3).reps("10").build();
        TrainingExerciseUpdateRequest request = TrainingExerciseUpdateRequest.builder().sets(4).reps("12").build();
        TrainingExerciseResponse response = TrainingExerciseResponse.builder().id(1L).sets(4).reps("12").build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(exercise));
        when(commandPort.update(exercise)).thenReturn(exercise);
        when(mapper.toResponse(exercise)).thenReturn(response);

        TrainingExerciseResponse result = useCase.execute(1L, request);

        assertSame(response, result);
        assertEquals(4, exercise.getSets());
        assertEquals("12", exercise.getReps());
        verify(commandPort).update(exercise);
    }

    @Test
    void executeShouldThrowWhenTrainingExerciseIsNotFound() {
        when(queryPort.findById(1L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L, TrainingExerciseUpdateRequest.builder().build()));

        assertEquals("Exercício de treinamento não encontrado", ex.getMessage());
    }
}

