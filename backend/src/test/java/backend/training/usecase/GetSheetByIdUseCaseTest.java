package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingSheet;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetSheetByIdUseCaseTest {

    @Mock
    private TrainingSheetQueryPort queryPort;

    @Mock
    private TrainingSheetMapper mapper;

    @InjectMocks
    private GetSheetByIdUseCase useCase;

    @Test
    void executeShouldReturnMappedSheetWhenFound() {
        TrainingSheet sheet = TrainingSheet.builder().id(1L).name("Ficha").build();
        TrainingSheetResponse response = TrainingSheetResponse.builder().id(1L).name("Ficha").build();

        when(queryPort.findById(1L)).thenReturn(Optional.of(sheet));
        when(mapper.toResponse(sheet)).thenReturn(response);

        TrainingSheetResponse result = useCase.execute(1L);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenSheetIsNotFound() {
        when(queryPort.findById(1L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Ficha de treino não encontrada", ex.getMessage());
    }
}

