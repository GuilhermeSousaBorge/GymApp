package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.mapper.TrainingExerciseMapper;
import backend.training.model.entity.TrainingExercise;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.port.TrainingSheetQueryPort;
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
class ListExercisesFromSheetUseCaseTest {

    @Mock
    private TrainingExerciseQueryPort exerciseQueryPort;

    @Mock
    private TrainingSheetQueryPort sheetQueryPort;

    @Mock
    private TrainingExerciseMapper mapper;

    @InjectMocks
    private ListExercisesFromSheetUseCase useCase;

    @Test
    void executeShouldReturnMappedExercisesWhenSheetExists() {
        TrainingExercise exercise = TrainingExercise.builder().id(1L).build();
        TrainingExerciseResponse response = TrainingExerciseResponse.builder().id(1L).build();

        when(sheetQueryPort.existsById(2L)).thenReturn(true);
        when(exerciseQueryPort.findBySheetWithExercise(2L)).thenReturn(List.of(exercise));
        when(mapper.toResponse(exercise)).thenReturn(response);

        List<TrainingExerciseResponse> result = useCase.execute(2L);

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    void executeShouldThrowWhenSheetDoesNotExist() {
        when(sheetQueryPort.existsById(2L)).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(2L));

        assertEquals("Folha não encontrada", ex.getMessage());
    }
}

