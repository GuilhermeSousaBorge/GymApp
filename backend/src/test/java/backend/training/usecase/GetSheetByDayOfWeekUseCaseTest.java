package backend.training.usecase;

import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingSheet;
import backend.training.model.enums.DayOfWeek;
import backend.training.port.TrainingSheetQueryPort;
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
class GetSheetByDayOfWeekUseCaseTest {

    @Mock
    private TrainingSheetQueryPort queryPort;

    @Mock
    private TrainingSheetMapper mapper;

    @InjectMocks
    private GetSheetByDayOfWeekUseCase useCase;

    @Test
    void executeShouldReturnMappedSheetsForDayOfWeek() {
        TrainingSheet sheet = TrainingSheet.builder().id(1L).name("Ficha").build();
        TrainingSheetResponse response = TrainingSheetResponse.builder().id(1L).name("Ficha").build();

        when(queryPort.findByDayOfWeek(DayOfWeek.MONDAY)).thenReturn(List.of(sheet));
        when(mapper.toResponse(sheet)).thenReturn(response);

        List<TrainingSheetResponse> result = useCase.execute(DayOfWeek.MONDAY);

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }
}

