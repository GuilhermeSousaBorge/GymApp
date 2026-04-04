package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseResponse;
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
class ReorderExerciseUseCaseTest {

    @Mock
    private TrainingExerciseQueryPort queryPort;

    @Mock
    private TrainingExerciseCommandPort commandPort;

    @Mock
    private TrainingExerciseMapper mapper;

    @InjectMocks
    private ReorderExerciseUseCase useCase;

    @Test
    void executeShouldUpdateOrderWhenNewOrderIsValid() {
        TrainingExercise exercise = TrainingExercise.builder().id(4L).orderInSheet(1).build();
        TrainingExerciseResponse response = TrainingExerciseResponse.builder().id(4L).orderInSheet(3).build();

        when(queryPort.findById(4L)).thenReturn(Optional.of(exercise));
        when(commandPort.update(exercise)).thenReturn(exercise);
        when(mapper.toResponse(exercise)).thenReturn(response);

        TrainingExerciseResponse result = useCase.execute(4L, 3);

        assertSame(response, result);
        assertEquals(3, exercise.getOrderInSheet());
        verify(commandPort).update(exercise);
    }

    @Test
    void executeShouldThrowWhenNewOrderIsInvalid() {
        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(4L, 0));

        assertEquals("Ordem deve ser maior que 0", ex.getMessage());
    }
}

