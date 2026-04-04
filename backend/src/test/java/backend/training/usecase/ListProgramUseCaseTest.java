package backend.training.usecase;

import backend.training.dto.TrainingProgramResponse;
import backend.training.mapper.TrainingProgramMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListProgramUseCaseTest {

    @Mock
    private TrainingProgramQueryPort queryPort;

    @Mock
    private TrainingProgramMapper mapper;

    @InjectMocks
    private ListProgramUseCase useCase;

    @Test
    void executeShouldFilterByUserWhenUserIdIsProvided() {
        TrainingProgram program = TrainingProgram.builder().id(1L).name("Programa").build();
        TrainingProgramResponse response = TrainingProgramResponse.builder().id(1L).name("Programa").build();

        when(queryPort.findByStudentId(9L)).thenReturn(List.of(program));
        when(mapper.toResponse(program)).thenReturn(response);

        List<TrainingProgramResponse> result = useCase.execute(9L);

        assertEquals(1, result.size());
        verify(queryPort).findByStudentId(9L);
        verify(queryPort, never()).findAll();
    }

    @Test
    void executeShouldListAllProgramsWhenUserIdIsNull() {
        TrainingProgram program = TrainingProgram.builder().id(1L).name("Programa").build();
        TrainingProgramResponse response = TrainingProgramResponse.builder().id(1L).name("Programa").build();

        when(queryPort.findAll()).thenReturn(List.of(program));
        when(mapper.toResponse(program)).thenReturn(response);

        List<TrainingProgramResponse> result = useCase.execute(null);

        assertEquals(1, result.size());
        verify(queryPort).findAll();
        verify(queryPort, never()).findByStudentId(9L);
    }
}

