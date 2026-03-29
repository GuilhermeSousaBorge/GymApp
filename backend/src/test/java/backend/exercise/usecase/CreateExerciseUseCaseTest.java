package backend.exercise.usecase;

import backend.exercise.dto.ExerciseRequest;
import backend.exercise.dto.ExerciseResponse;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.exercise.port.ExerciseCommandPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateExerciseUseCaseTest {

    @Mock
    private ExerciseCommandPort commandPort;

    @Mock
    private ExerciseValidationPort validationPort;

    @Mock
    private ExerciseCategoryQueryPort categoryQueryPort;

    @Mock
    private ExerciseMapper mapper;

    @InjectMocks
    private CreateExerciseUseCase useCase;

    @Test
    void executeShouldCreateExerciseWhenCategoryIsActive() {
        ExerciseRequest request = ExerciseRequest.builder()
                .name("Supino")
                .description("Peito")
                .categoryId(1L)
                .build();
        ExerciseCategory category = ExerciseCategory.builder().id(1L).active(true).build();
        Exercise saved = Exercise.builder().id(5L).name("Supino").category(category).active(true).build();
        ExerciseResponse response = ExerciseResponse.builder().id(5L).name("Supino").build();

        when(validationPort.existsByName("Supino")).thenReturn(false);
        when(categoryQueryPort.findById(1L)).thenReturn(Optional.of(category));
        when(commandPort.save(any(Exercise.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ExerciseResponse result = useCase.execute(request);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenCategoryIsInactive() {
        ExerciseRequest request = ExerciseRequest.builder().name("Supino").categoryId(1L).build();
        ExerciseCategory category = ExerciseCategory.builder().id(1L).active(false).build();

        when(validationPort.existsByName("Supino")).thenReturn(false);
        when(categoryQueryPort.findById(1L)).thenReturn(Optional.of(category));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Não é possível criar exercício em categoria inativa", ex.getMessage());
    }
}

