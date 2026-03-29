package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingProgramResponse;
import backend.training.dto.TrainingProgramUpdateRequest;
import backend.training.mapper.TrainingProgramMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramCommandPort;
import backend.training.port.TrainingProgramQueryPort;
import backend.training.port.TrainingProgramValidationPort;
import backend.user.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProgramUseCaseTest {

    @Mock
    private TrainingProgramQueryPort queryPort;

    @Mock
    private TrainingProgramValidationPort validationPort;

    @Mock
    private TrainingProgramCommandPort commandPort;

    @Mock
    private TrainingProgramMapper mapper;

    @InjectMocks
    private UpdateProgramUseCase useCase;

    @Test
    void executeShouldUpdateProgramWhenNameIsAvailable() {
        User student = User.builder().id(1L).build();
        TrainingProgram program = TrainingProgram.builder()
                .id(5L)
                .name("Original")
                .description("Desc")
                .student(student)
                .build();
        TrainingProgramUpdateRequest request = TrainingProgramUpdateRequest.builder()
                .name("Novo Nome")
                .description("Nova Desc")
                .build();
        TrainingProgramResponse response = TrainingProgramResponse.builder().id(5L).name("Novo Nome").build();

        when(queryPort.findById(5L)).thenReturn(Optional.of(program));
        when(validationPort.existsByNameAndStudent("Novo Nome", 1L)).thenReturn(false);
        when(commandPort.update(program)).thenReturn(program);
        when(mapper.toResponse(program)).thenReturn(response);

        TrainingProgramResponse result = useCase.execute(5L, request);

        assertSame(response, result);
        assertEquals("Novo Nome", program.getName());
        assertEquals("Nova Desc", program.getDescription());
        verify(commandPort).update(program);
    }

    @Test
    void executeShouldThrowWhenNewNameAlreadyExistsForStudent() {
        User student = User.builder().id(1L).build();
        TrainingProgram program = TrainingProgram.builder().id(5L).name("Original").student(student).build();
        TrainingProgramUpdateRequest request = TrainingProgramUpdateRequest.builder().name("Duplicado").build();

        when(queryPort.findById(5L)).thenReturn(Optional.of(program));
        when(validationPort.existsByNameAndStudent("Duplicado", 1L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(5L, request));

        assertEquals("Já existe um programa com este nome para este aluno", ex.getMessage());
    }
}

