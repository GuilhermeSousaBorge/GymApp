package backend.dashboard.usecase;

import backend.dashboard.dto.StudentDashboardResponse;
import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.training.model.enums.DayOfWeek;
import backend.training.port.TrainingProgramQueryPort;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetStudentDashboardUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private TrainingProgramQueryPort trainingProgramQueryPort;

    @Mock
    private TrainingSheetMapper sheetMapper;

    @InjectMocks
    private GetStudentDashboardUseCase useCase;

    @Test
    void executeShouldThrowWhenStudentIsNotFound() {
        when(userQueryPort.findById(404L)).thenReturn(java.util.Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(404L));

        assertEquals("Aluno nao encontrado", ex.getMessage());
    }

    @Test
    void executeShouldBuildStudentDashboardWithTodayAndNextTrainings() {
        Long userId = 10L;
        User user = User.builder().id(userId).name("Aluno").build();

        DayOfWeek today = DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name());
        DayOfWeek tomorrow = DayOfWeek.valueOf(LocalDate.now().plusDays(1).getDayOfWeek().name());

        TrainingSheet todaySheet = TrainingSheet.builder()
                .id(1L)
                .name("Treino de hoje")
                .weekdays(Set.of(today))
                .build();

        TrainingSheet nextSheet = TrainingSheet.builder()
                .id(2L)
                .name("Treino de amanha")
                .weekdays(Set.of(tomorrow))
                .build();

        TrainingProgram program = TrainingProgram.builder()
                .id(100L)
                .name("Programa A")
                .active(true)
                .trainingSheets(List.of(todaySheet, nextSheet))
                .build();

        TrainingSheetResponse todayResponse = TrainingSheetResponse.builder().id(1L).name("Treino de hoje").build();
        TrainingSheetResponse nextResponse = TrainingSheetResponse.builder().id(2L).name("Treino de amanha").build();

        when(userQueryPort.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(trainingProgramQueryPort.findByStudentId(userId)).thenReturn(List.of(program));
        when(sheetMapper.toResponse(todaySheet)).thenReturn(todayResponse);
        when(sheetMapper.toResponse(nextSheet)).thenReturn(nextResponse);

        StudentDashboardResponse result = useCase.execute(userId);

        assertNotNull(result);
        assertEquals(1, result.getPrograms().size());

        StudentDashboardResponse.StudentProgramSummary summary = result.getPrograms().get(0);
        assertEquals(100L, summary.getId());
        assertEquals("Programa A", summary.getName());
        assertNotNull(summary.getTodaySheet());
        assertEquals(1L, summary.getTodaySheet().getId());
        assertEquals(2, summary.getNextTrainings().size());
    }

    @Test
    void executeShouldHandleProgramWithoutSheets() {
        Long userId = 11L;
        User user = User.builder().id(userId).name("Aluno sem fichas").build();

        TrainingProgram program = TrainingProgram.builder()
                .id(200L)
                .name("Programa sem fichas")
                .active(true)
                .trainingSheets(null)
                .build();

        when(userQueryPort.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(trainingProgramQueryPort.findByStudentId(userId)).thenReturn(List.of(program));

        StudentDashboardResponse result = useCase.execute(userId);

        assertNotNull(result);
        assertEquals(1, result.getPrograms().size());
        assertEquals(0, result.getPrograms().get(0).getNextTrainings().size());
        assertNull(result.getPrograms().get(0).getTodaySheet());
    }
}

