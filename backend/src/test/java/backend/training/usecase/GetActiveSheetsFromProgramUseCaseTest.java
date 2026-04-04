package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingProgramQueryPort;
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
class GetActiveSheetsFromProgramUseCaseTest {

    @Mock
    private TrainingSheetQueryPort sheetQueryPort;

    @Mock
    private TrainingProgramQueryPort programQueryPort;

    @Mock
    private TrainingSheetMapper mapper;

    @InjectMocks
    private GetActiveSheetsFromProgramUseCase useCase;

    @Test
    void executeShouldReturnMappedActiveSheetsWhenProgramExists() {
        TrainingSheet sheet = TrainingSheet.builder().id(1L).name("Ficha").active(true).build();
        TrainingSheetResponse response = TrainingSheetResponse.builder().id(1L).name("Ficha").active(true).build();

        when(programQueryPort.findById(7L)).thenReturn(java.util.Optional.of(TrainingProgram.builder().id(7L).build()));
        when(sheetQueryPort.findByProgramIdAndActive(7L)).thenReturn(List.of(sheet));
        when(mapper.toResponse(sheet)).thenReturn(response);

        List<TrainingSheetResponse> result = useCase.execute(7L);

        assertEquals(1, result.size());
        assertSame(response, result.get(0));
    }

    @Test
    void executeShouldThrowWhenProgramDoesNotExist() {
        when(programQueryPort.findById(7L)).thenReturn(java.util.Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(7L));

        assertEquals("Programa não encontrado", ex.getMessage());
    }
}

