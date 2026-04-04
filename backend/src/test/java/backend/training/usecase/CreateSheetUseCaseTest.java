package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetRequest;
import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.training.model.enums.DayOfWeek;
import backend.training.port.TrainingProgramQueryPort;
import backend.training.port.TrainingSheetCommandPort;
import backend.training.port.TrainingSheetQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSheetUseCaseTest {

    @Mock
    private TrainingSheetQueryPort sheetQueryPort;

    @Mock
    private TrainingSheetCommandPort sheetCommandPort;

    @Mock
    private TrainingProgramQueryPort programQueryPort;

    @Mock
    private TrainingSheetMapper mapper;

    @InjectMocks
    private CreateSheetUseCase useCase;

    @Test
    void executeShouldCreateSheetWhenProgramIsActiveAndWeekdaysValid() {
        TrainingSheetRequest request = TrainingSheetRequest.builder()
                .name("Treino A")
                .description("Peito")
                .trainingProgramId(1L)
                .weekDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY))
                .build();

        TrainingProgram program = TrainingProgram.builder().id(1L).active(true).build();
        TrainingSheet saved = TrainingSheet.builder().id(8L).name("Treino A").trainingProgram(program).build();
        TrainingSheetResponse response = TrainingSheetResponse.builder().id(8L).name("Treino A").build();

        when(sheetQueryPort.existsByName("Treino A")).thenReturn(false);
        when(programQueryPort.findById(1L)).thenReturn(Optional.of(program));
        when(sheetQueryPort.countByProgramId(1L)).thenReturn(0);
        when(sheetCommandPort.save(any(TrainingSheet.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        TrainingSheetResponse result = useCase.execute(request);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenProgramIsInactive() {
        TrainingSheetRequest request = TrainingSheetRequest.builder()
                .name("Treino A")
                .description("Peito")
                .trainingProgramId(1L)
                .weekDays(Set.of(DayOfWeek.MONDAY))
                .build();
        TrainingProgram program = TrainingProgram.builder().id(1L).active(false).build();

        when(sheetQueryPort.existsByName("Treino A")).thenReturn(false);
        when(programQueryPort.findById(1L)).thenReturn(Optional.of(program));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Não é possível criar ficha de treino para programa inativo", ex.getMessage());
    }
}

