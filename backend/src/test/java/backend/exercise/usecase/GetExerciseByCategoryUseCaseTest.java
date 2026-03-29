package backend.exercise.usecase;

import backend.exercise.dto.ExerciseResponse;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.exercise.port.ExerciseQueryPort;
import backend.infrastructure.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetExerciseByCategoryUseCaseTest {

    @Mock
    private ExerciseQueryPort queryPort;

    @Mock
    private ExerciseCategoryQueryPort categoryQueryPort;

    @Mock
    private ExerciseMapper mapper;

    @InjectMocks
    private GetExerciseByCategoryUseCase useCase;

    @Test
    void executeShouldReturnMappedExercisesWhenCategoryExists() {
        Exercise exercise = Exercise.builder().id(1L).name("Supino").build();
        ExerciseResponse response = ExerciseResponse.builder().id(1L).name("Supino").build();

        when(categoryQueryPort.findById(5L)).thenReturn(java.util.Optional.of(ExerciseCategory.builder().id(5L).build()));
        when(queryPort.findByCategoryActive(5L)).thenReturn(List.of(exercise));
        when(mapper.toResponse(exercise)).thenReturn(response);

        List<ExerciseResponse> result = useCase.execute(5L);

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    void executeShouldThrowWhenCategoryDoesNotExist() {
        when(categoryQueryPort.findById(5L)).thenReturn(java.util.Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(5L));

        assertEquals("Categoria não encontrada", ex.getMessage());
    }
}

