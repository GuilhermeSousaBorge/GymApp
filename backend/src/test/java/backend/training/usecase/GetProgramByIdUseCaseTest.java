package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingProgramResponse;
import backend.training.mapper.TrainingProgramMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramQueryPort;
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
class GetProgramByIdUseCaseTest {

    @Mock
    private TrainingProgramQueryPort queryPort;

    @Mock
    private TrainingProgramMapper mapper;

    @InjectMocks
    private GetProgramByIdUseCase useCase;

    @Test
    void executeShouldReturnMappedProgramWhenFound() {
        TrainingProgram program = TrainingProgram.builder().id(1L).name("Programa").build();
        TrainingProgramResponse response = TrainingProgramResponse.builder().id(1L).name("Programa").build();

        when(queryPort.findByIdWithSheets(1L)).thenReturn(Optional.of(program));
        when(mapper.toResponse(program)).thenReturn(response);

        TrainingProgramResponse result = useCase.execute(1L);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenProgramIsNotFound() {
        when(queryPort.findByIdWithSheets(1L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(1L));

        assertEquals("Programa de treinamento não encontrado", ex.getMessage());
    }
}

