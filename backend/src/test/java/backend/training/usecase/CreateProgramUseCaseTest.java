package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingProgramRequest;
import backend.training.dto.TrainingProgramResponse;
import backend.training.mapper.TrainingProgramMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingProgramCommandPort;
import backend.training.port.TrainingProgramValidationPort;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProgramUseCaseTest {

    @Mock
    private TrainingProgramCommandPort commandPort;

    @Mock
    private TrainingProgramValidationPort validationPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private TrainingProgramMapper mapper;

    @InjectMocks
    private CreateProgramUseCase useCase;

    @Test
    void executeShouldCreateProgramWhenRequestIsValid() {
        TrainingProgramRequest request = TrainingProgramRequest.builder()
                .name("Hipertrofia")
                .description("Programa A")
                .userId(1L)
                .trainerId(2L)
                .build();

        User student = User.builder().id(1L).name("Aluno").build();
        User trainer = User.builder().id(2L).name("Trainer").build();
        TrainingProgram saved = TrainingProgram.builder().id(10L).name("Hipertrofia").student(student).trainer(trainer).active(true).build();
        TrainingProgramResponse response = TrainingProgramResponse.builder().id(10L).name("Hipertrofia").build();

        when(validationPort.existsByNameAndStudent("Hipertrofia", 1L)).thenReturn(false);
        when(userQueryPort.findById(1L)).thenReturn(Optional.of(student));
        when(userQueryPort.findById(2L)).thenReturn(Optional.of(trainer));
        when(commandPort.save(any(TrainingProgram.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        TrainingProgramResponse result = useCase.execute(request);

        assertSame(response, result);
        verify(commandPort).save(any(TrainingProgram.class));
    }

    @Test
    void executeShouldThrowWhenProgramNameAlreadyExistsForStudent() {
        TrainingProgramRequest request = TrainingProgramRequest.builder()
                .name("Hipertrofia")
                .description("Programa A")
                .userId(1L)
                .build();

        when(validationPort.existsByNameAndStudent("Hipertrofia", 1L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Já existe um programa de treinamento com este nome para este aluno", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenStudentIsNotFound() {
        TrainingProgramRequest request = TrainingProgramRequest.builder()
                .name("Hipertrofia")
                .description("Programa A")
                .userId(1L)
                .build();

        when(validationPort.existsByNameAndStudent("Hipertrofia", 1L)).thenReturn(false);
        when(userQueryPort.findById(1L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Aluno não encontrado ou inativo", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenTrainerIsNotFound() {
        TrainingProgramRequest request = TrainingProgramRequest.builder()
                .name("Hipertrofia")
                .description("Programa A")
                .userId(1L)
                .trainerId(2L)
                .build();

        User student = User.builder().id(1L).name("Aluno").build();

        when(validationPort.existsByNameAndStudent("Hipertrofia", 1L)).thenReturn(false);
        when(userQueryPort.findById(1L)).thenReturn(Optional.of(student));
        when(userQueryPort.findById(2L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Personal não encontrado ou inativo", ex.getMessage());
    }
}

