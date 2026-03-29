package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.mapper.TrainingExerciseMapper;
import backend.training.model.entity.TrainingExercise;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExerciseByIdUseCaseTest {

    @Mock
    private TrainingExerciseQueryPort queryPort;

    @Mock
    private TrainingExerciseMapper mapper;

    @InjectMocks
    private GetExerciseByIdUseCase useCase;

    @Test
    void executeShouldReturnMappedTrainingExerciseWhenFound() {
        TrainingExercise exercise = TrainingExercise.builder().id(1L).build();
        TrainingExerciseResponse response = TrainingExerciseResponse.builder().id(1L).build();

        when(queryPort.findByIdWithExerciseDetails(1L)).thenReturn(Optional.of(exercise));
        when(mapper.toResponse(exercise)).thenReturn(response);

        TrainingExerciseResponse result = useCase.execute(1L);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenTrainingExerciseIsNotFound() {
        when(queryPort.findByIdWithExerciseDetails(1L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Exercício de treinamento não encontrado", ex.getMessage());
    }
}

