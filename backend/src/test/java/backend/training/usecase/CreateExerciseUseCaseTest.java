package backend.training.usecase;

import backend.exercise.model.entity.Exercise;
import backend.exercise.port.ExerciseQueryPort;
import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseRequest;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.mapper.TrainingExerciseMapper;
import backend.training.model.entity.TrainingExercise;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingExerciseCommandPort;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.port.TrainingSheetQueryPort;
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
    private TrainingExerciseQueryPort exerciseQueryPort;

    @Mock
    private TrainingExerciseCommandPort exerciseCommandPort;

    @Mock
    private TrainingSheetQueryPort sheetQueryPort;

    @Mock
    private ExerciseQueryPort baseExerciseQueryPort;

    @Mock
    private TrainingExerciseMapper mapper;

    @InjectMocks
    private CreateExerciseUseCase useCase;

    @Test
    void executeShouldCreateTrainingExerciseWhenSheetAndExerciseAreActive() {
        TrainingExerciseRequest request = TrainingExerciseRequest.builder()
                .trainingSheetId(1L)
                .exerciseId(2L)
                .sets(4)
                .reps("12")
                .restTimeInSeconds(60)
                .build();

        TrainingSheet sheet = TrainingSheet.builder().id(1L).active(true).build();
        Exercise exercise = Exercise.builder().id(2L).active(true).build();
        TrainingExerciseResponse response = TrainingExerciseResponse.builder().id(7L).exerciseId(2L).trainingSheetId(1L).build();

        when(sheetQueryPort.findById(1L)).thenReturn(Optional.of(sheet));
        when(baseExerciseQueryPort.findById(2L)).thenReturn(Optional.of(exercise));
        when(exerciseQueryPort.existsBySheetAndExercise(1L, 2L)).thenReturn(false);
        when(exerciseQueryPort.countBySheet(1L)).thenReturn(0);
        when(exerciseCommandPort.save(any(TrainingExercise.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(TrainingExercise.class))).thenReturn(response);

        TrainingExerciseResponse result = useCase.execute(request);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenExerciseAlreadyExistsInSheet() {
        TrainingExerciseRequest request = TrainingExerciseRequest.builder()
                .trainingSheetId(1L)
                .exerciseId(2L)
                .sets(4)
                .reps("12")
                .restTimeInSeconds(60)
                .build();

        TrainingSheet sheet = TrainingSheet.builder().id(1L).active(true).build();
        Exercise exercise = Exercise.builder().id(2L).active(true).build();

        when(sheetQueryPort.findById(1L)).thenReturn(Optional.of(sheet));
        when(baseExerciseQueryPort.findById(2L)).thenReturn(Optional.of(exercise));
        when(exerciseQueryPort.existsBySheetAndExercise(1L, 2L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Exercício já adicionado à ficha de treinamento", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenSheetIsInactive() {
        TrainingExerciseRequest request = TrainingExerciseRequest.builder()
                .trainingSheetId(1L)
                .exerciseId(2L)
                .sets(4)
                .reps("12")
                .restTimeInSeconds(60)
                .build();

        TrainingSheet inactiveSheet = TrainingSheet.builder().id(1L).active(false).build();

        when(sheetQueryPort.findById(1L)).thenReturn(Optional.of(inactiveSheet));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Ficha de treinamento inativa", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenExerciseIsInactive() {
        TrainingExerciseRequest request = TrainingExerciseRequest.builder()
                .trainingSheetId(1L)
                .exerciseId(2L)
                .sets(4)
                .reps("12")
                .restTimeInSeconds(60)
                .build();

        TrainingSheet activeSheet = TrainingSheet.builder().id(1L).active(true).build();
        Exercise inactiveExercise = Exercise.builder().id(2L).active(false).build();

        when(sheetQueryPort.findById(1L)).thenReturn(Optional.of(activeSheet));
        when(baseExerciseQueryPort.findById(2L)).thenReturn(Optional.of(inactiveExercise));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Exercício inativo", ex.getMessage());
    }
}

