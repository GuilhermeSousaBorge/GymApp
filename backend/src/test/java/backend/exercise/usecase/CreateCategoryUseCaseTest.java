package backend.exercise.usecase;

import backend.exercise.dto.ExerciseCategoryRequest;
import backend.exercise.dto.ExerciseCategoryResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateCategoryUseCaseTest {

    @Mock
    private ExerciseCategoryQueryPort queryPort;

    @Mock
    private ExerciseCategoryCommandPort commandPort;

    @Mock
    private ExerciseCategoryMapper mapper;

    @InjectMocks
    private CreateCategoryUseCase useCase;

    @Test
    void executeShouldCreateCategoryWhenMuscleGroupIsAvailable() {
        ExerciseCategoryRequest request = ExerciseCategoryRequest.builder().muscleGroup("Peito").description("Peitoral").build();
        ExerciseCategory saved = ExerciseCategory.builder().id(1L).muscleGroup("Peito").active(true).build();
        ExerciseCategoryResponse response = ExerciseCategoryResponse.builder().id(1L).muscleGroup("Peito").build();

        when(queryPort.findByName("Peito")).thenReturn(java.util.Optional.empty());
        when(commandPort.save(any(ExerciseCategory.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        ExerciseCategoryResponse result = useCase.execute(request);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenCategoryAlreadyExists() {
        ExerciseCategoryRequest request = ExerciseCategoryRequest.builder().muscleGroup("Peito").build();
        when(queryPort.findByName("Peito")).thenReturn(java.util.Optional.of(ExerciseCategory.builder().id(99L).muscleGroup("Peito").build()));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Categoria com este nome já existe", ex.getMessage());
    }
}

