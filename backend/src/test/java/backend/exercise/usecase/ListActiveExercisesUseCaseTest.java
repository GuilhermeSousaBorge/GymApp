package backend.exercise.usecase;

import backend.exercise.dto.ExerciseResponse;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.port.ExerciseQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListActiveExercisesUseCaseTest {

    @Mock
    private ExerciseQueryPort queryPort;

    @Mock
    private ExerciseMapper mapper;

    @InjectMocks
    private ListActiveExercisesUseCase useCase;

    @Test
    void executeShouldReturnMappedActiveExercises() {
        Exercise exercise = Exercise.builder().id(1L).name("Supino").active(true).build();
        ExerciseResponse response = ExerciseResponse.builder().id(1L).name("Supino").active(true).build();

        when(queryPort.findAllActive()).thenReturn(List.of(exercise));
        when(mapper.toResponse(exercise)).thenReturn(response);

        List<ExerciseResponse> result = useCase.execute();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }
}

