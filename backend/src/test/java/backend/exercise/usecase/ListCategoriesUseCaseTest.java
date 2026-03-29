package backend.exercise.usecase;

import backend.exercise.dto.ExerciseCategoryResponse;
import backend.exercise.mapper.ExerciseCategoryMapper;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryQueryPort;
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
class ListCategoriesUseCaseTest {

    @Mock
    private ExerciseCategoryQueryPort queryPort;

    @Mock
    private ExerciseCategoryMapper mapper;

    @InjectMocks
    private ListCategoriesUseCase useCase;

    @Test
    void executeShouldReturnMappedCategories() {
        ExerciseCategory category = ExerciseCategory.builder().id(1L).muscleGroup("Peito").build();
        ExerciseCategoryResponse response = ExerciseCategoryResponse.builder().id(1L).muscleGroup("Peito").build();

        when(queryPort.findAll()).thenReturn(List.of(category));
        when(mapper.toResponse(category)).thenReturn(response);

        List<ExerciseCategoryResponse> result = useCase.execute();

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }
}

