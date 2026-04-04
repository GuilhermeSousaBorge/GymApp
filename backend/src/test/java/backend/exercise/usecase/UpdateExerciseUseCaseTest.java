package backend.exercise.usecase;

import backend.exercise.dto.ExerciseResponse;
import backend.exercise.dto.ExerciseUpdateRequest;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.exercise.port.ExerciseCommandPort;
import backend.exercise.port.ExerciseQueryPort;
import backend.exercise.port.ExerciseValidationPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateExerciseUseCaseTest {

    @Mock
    private ExerciseQueryPort queryPort;

    @Mock
    private ExerciseCommandPort commandPort;

    @Mock
    private ExerciseValidationPort validationPort;

    @Mock
    private ExerciseCategoryQueryPort categoryQueryPort;

    @Mock
    private ExerciseMapper mapper;

    @InjectMocks
    private UpdateExerciseUseCase useCase;

    @Test
    void executeShouldUpdateExerciseWhenRequestIsValid() {
        ExerciseCategory currentCategory = ExerciseCategory.builder().id(1L).active(true).build();
        ExerciseCategory newCategory = ExerciseCategory.builder().id(2L).active(true).build();
        Exercise exercise = Exercise.builder().id(9L).name("Supino").category(currentCategory).active(true).build();
        ExerciseUpdateRequest request = ExerciseUpdateRequest.builder().name("Supino Inclinado").categoryId(2L).active(false).build();
        ExerciseResponse response = ExerciseResponse.builder().id(9L).name("Supino Inclinado").build();

        when(queryPort.findByIdWithCategory(9L)).thenReturn(Optional.of(exercise));
        when(validationPort.existsByName("Supino Inclinado")).thenReturn(false);
        when(categoryQueryPort.findById(2L)).thenReturn(Optional.of(newCategory));
        when(commandPort.update(exercise)).thenReturn(exercise);
        when(mapper.toResponse(exercise)).thenReturn(response);

        ExerciseResponse result = useCase.execute(9L, request);

        assertSame(response, result);
        assertEquals("Supino Inclinado", exercise.getName());
        assertSame(newCategory, exercise.getCategory());
        assertEquals(false, exercise.getActive());
        verify(commandPort).update(exercise);
    }

    @Test
    void executeShouldThrowWhenNewNameAlreadyExists() {
        ExerciseCategory category = ExerciseCategory.builder().id(1L).active(true).build();
        Exercise exercise = Exercise.builder().id(9L).name("Supino").category(category).build();
        ExerciseUpdateRequest request = ExerciseUpdateRequest.builder().name("Duplicado").build();

        when(queryPort.findByIdWithCategory(9L)).thenReturn(Optional.of(exercise));
        when(validationPort.existsByName("Duplicado")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(9L, request));

        assertEquals("Exercício com este nome já existe", ex.getMessage());
    }
}

