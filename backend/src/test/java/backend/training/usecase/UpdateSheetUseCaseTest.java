package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
import backend.training.dto.TrainingSheetUpdateRequest;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.training.model.enums.DayOfWeek;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateSheetUseCaseTest {

    @Mock
    private TrainingSheetQueryPort queryPort;

    @Mock
    private TrainingSheetCommandPort commandPort;

    @Mock
    private TrainingSheetMapper mapper;

    @InjectMocks
    private UpdateSheetUseCase useCase;

    @Test
    void executeShouldUpdateSheetWhenRequestIsValid() {
        TrainingProgram program = TrainingProgram.builder().id(7L).build();
        TrainingSheet sheet = TrainingSheet.builder().id(1L).name("Original").trainingProgram(program).build();
        TrainingSheetUpdateRequest request = TrainingSheetUpdateRequest.builder()
                .name("Nova")
                .weekdays(Set.of(DayOfWeek.MONDAY))
                .build();
        TrainingSheetResponse response = TrainingSheetResponse.builder().id(1L).name("Nova").build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(sheet));
        when(queryPort.existsByNameAndProgramId("Nova", 7L)).thenReturn(false);
        when(commandPort.update(sheet)).thenReturn(sheet);
        when(mapper.toResponse(sheet)).thenReturn(response);

        TrainingSheetResponse result = useCase.execute(1L, request);

        assertSame(response, result);
        assertEquals("Nova", sheet.getName());
        verify(commandPort).update(sheet);
    }

    @Test
    void executeShouldThrowWhenWeekdaysAreEmpty() {
        TrainingProgram program = TrainingProgram.builder().id(7L).build();
        TrainingSheet sheet = TrainingSheet.builder().id(1L).name("Original").trainingProgram(program).build();
        TrainingSheetUpdateRequest request = TrainingSheetUpdateRequest.builder().weekdays(Set.of()).build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(sheet));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L, request));

        assertEquals("Folha deve ter pelo menos 1 dia de treino", ex.getMessage());
    }
}

