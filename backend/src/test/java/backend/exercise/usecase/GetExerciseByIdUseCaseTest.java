package backend.exercise.usecase;

import backend.exercise.dto.ExerciseResponse;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.port.ExerciseQueryPort;
import backend.infrastructure.exception.BadRequestException;
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
    private ExerciseQueryPort queryPort;

    @Mock
    private ExerciseMapper mapper;

    @InjectMocks
    private GetExerciseByIdUseCase useCase;

    @Test
    void executeShouldReturnMappedExerciseWhenFound() {
        Exercise exercise = Exercise.builder().id(1L).name("Supino").build();
        ExerciseResponse response = ExerciseResponse.builder().id(1L).name("Supino").build();

        when(queryPort.findByIdWithCategory(1L)).thenReturn(Optional.of(exercise));
        when(mapper.toResponse(exercise)).thenReturn(response);

        ExerciseResponse result = useCase.execute(1L);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenExerciseIsNotFound() {
        when(queryPort.findByIdWithCategory(1L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Exercício não encontrado", ex.getMessage());
    }
}

