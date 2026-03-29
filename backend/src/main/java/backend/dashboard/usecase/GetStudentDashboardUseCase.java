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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetStudentDashboardUseCase {

    private final UserQueryPort userQueryPort;
    private final TrainingProgramQueryPort trainingProgramQueryPort;
    private final TrainingSheetMapper sheetMapper;

    @Transactional(readOnly = true)
    public StudentDashboardResponse execute(Long userId){
        log.info("Buscando dashboard do aluno com ID: {}", userId);
        // 1. programas do aluno
        // 2. próximos treinos da semana
        // 3. ficha do dia atual
        String today = LocalDate.now().getDayOfWeek().name();

        User user = userQueryPort.findById(userId).orElseThrow(() -> new BadRequestException("Aluno nao encontrado"));

        List<TrainingProgram> trainingPrograms = trainingProgramQueryPort.findByStudentId(user.getId());

        List<DayOfWeek> nextSevenDays = IntStream.range(0, 7)
                .mapToObj(i -> LocalDate.now().plusDays(i).getDayOfWeek().name())
                .map(DayOfWeek::valueOf)
                .toList();

        List<StudentDashboardResponse.StudentProgramSummary> programs = trainingPrograms.stream()
                .map(program -> {
                    List<TrainingSheet> sheets = program.getTrainingSheets() == null
                            ? Collections.emptyList()
                            : program.getTrainingSheets();

                    TrainingSheet todaySheet = sheets.stream()
                            .filter(sheet -> sheet.getWeekdays() != null && sheet.getWeekdays().stream().anyMatch(day -> day.name().equals(today))
                            ).findFirst().orElse(null);

                    List<TrainingSheetResponse> nextTrainings = sheets.stream().filter(sheet -> sheet.getWeekdays() != null
                                    && sheet.getWeekdays().stream().anyMatch(nextSevenDays::contains))
                            .map(sheetMapper::toResponse)
                            .toList();
                    return StudentDashboardResponse.StudentProgramSummary.builder()
                            .id(program.getId())
                            .name(program.getName())
                            .active(program.getActive())
                            .todaySheet(todaySheet != null ? sheetMapper.toResponse(todaySheet) : null)
                            .nextTrainings(nextTrainings)
                            .build();
                }).toList();
        return StudentDashboardResponse.builder().programs(programs).build();
    }
}
