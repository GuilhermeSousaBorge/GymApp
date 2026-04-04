package backend.exercise.usecase;

import backend.exercise.dto.ExerciseCategoryResponse;
import backend.exercise.dto.ExerciseCategoryUpdateRequest;
import backend.exercise.mapper.ExerciseCategoryMapper;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryCommandPort;
import backend.exercise.port.ExerciseCategoryQueryPort;
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
class UpdateCategoryUseCaseTest {

    @Mock
    private ExerciseCategoryQueryPort queryPort;

    @Mock
    private ExerciseCategoryCommandPort commandPort;

    @Mock
    private ExerciseCategoryMapper mapper;

    @InjectMocks
    private UpdateCategoryUseCase useCase;

    @Test
    void executeShouldUpdateCategoryWhenMuscleGroupIsAvailable() {
        ExerciseCategory category = ExerciseCategory.builder().id(1L).muscleGroup("Peito").active(true).build();
        ExerciseCategoryUpdateRequest request = ExerciseCategoryUpdateRequest.builder().muscleGroup("Costas").active(false).build();
        ExerciseCategoryResponse response = ExerciseCategoryResponse.builder().id(1L).muscleGroup("Costas").build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(category));
        when(queryPort.findByName("Costas")).thenReturn(Optional.empty());
        when(commandPort.update(category)).thenReturn(category);
        when(mapper.toResponse(category)).thenReturn(response);

        ExerciseCategoryResponse result = useCase.execute(1L, request);

        assertSame(response, result);
        assertEquals("Costas", category.getMuscleGroup());
        assertEquals(false, category.getActive());
        verify(commandPort).update(category);
    }

    @Test
    void executeShouldThrowWhenMuscleGroupAlreadyExists() {
        ExerciseCategory category = ExerciseCategory.builder().id(1L).muscleGroup("Peito").build();
        ExerciseCategoryUpdateRequest request = ExerciseCategoryUpdateRequest.builder().muscleGroup("Costas").build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(category));
        when(queryPort.findByName("Costas")).thenReturn(Optional.of(ExerciseCategory.builder().id(2L).muscleGroup("Costas").build()));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L, request));

        assertEquals("Categoria com este nome já existe", ex.getMessage());
    }
}

