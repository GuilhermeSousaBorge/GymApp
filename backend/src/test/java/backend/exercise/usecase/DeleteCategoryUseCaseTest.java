package backend.exercise.usecase;

import backend.exercise.model.entity.Exercise;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.port.ExerciseCategoryCommandPort;
import backend.exercise.port.ExerciseCategoryQueryPort;
import backend.infrastructure.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteCategoryUseCaseTest {

    @Mock
    private ExerciseCategoryQueryPort queryPort;

    @Mock
    private ExerciseCategoryCommandPort commandPort;

    @InjectMocks
    private DeleteCategoryUseCase useCase;

    @Test
    void executeShouldDeleteCategoryWhenItHasNoExercises() {
        ExerciseCategory category = ExerciseCategory.builder().id(1L).muscleGroup("Peito").exercises(List.of()).build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(category));

        useCase.execute(1L);

        verify(commandPort).deleteById(1L);
    }

    @Test
    void executeShouldThrowWhenCategoryHasAssociatedExercises() {
        ExerciseCategory category = ExerciseCategory.builder()
                .id(1L)
                .muscleGroup("Peito")
                .exercises(List.of(Exercise.builder().id(2L).build()))
                .build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(category));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Não é possível deletar categoria com exercícios associados", ex.getMessage());
    }

    @Test
    void executeShouldDeleteCategoryWhenExercisesListIsNull() {
        ExerciseCategory category = ExerciseCategory.builder().id(1L).muscleGroup("Peito").exercises(null).build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(category));

        useCase.execute(1L);

        verify(commandPort).deleteById(1L);
    }

    @Test
    void executeShouldThrowWhenCategoryIsNotFound() {
        when(queryPort.findById(42L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(42L));

        assertEquals("Categoria não encontrada", ex.getMessage());
        verify(commandPort, never()).deleteById(42L);
    }
}

