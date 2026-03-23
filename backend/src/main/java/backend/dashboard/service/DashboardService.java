package backend.dashboard.service;


import backend.dashboard.dto.AdminDashboardResponse;
import backend.dashboard.dto.StudentDashboardResponse;
import backend.training.dto.TrainingSheetResponse;
import backend.infrastructure.exception.BadRequestException;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.user.model.entity.User;
import backend.training.model.enums.DayOfWeek;
import backend.training.repository.TrainingProgramRepository;
import backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingSheetMapper sheetMapper;

    public AdminDashboardResponse getAdminDashboard() {
        // 1. total de alunos ativos
        // 2. total de programas ativos
        // 3. novos alunos no mês
        // 4. alunos sem programa de treino
        LocalDate today = LocalDate.now();

        int studentsActives = userRepository.countByActiveTrue();
        int trainingProgramsActives = trainingProgramRepository.countByActiveTrue();
        long withoutPrograms = userRepository.countStudentsWithoutProgram();
        int studentsThisMonth = userRepository
                .countByCreatedAtBetween(today.withDayOfMonth(1).atStartOfDay(),
                        today.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59));

        return AdminDashboardResponse.builder()
                .newStudentsThisMonth(studentsThisMonth)
                .totalActivePrograms(trainingProgramsActives)
                .studentsWithoutProgram(withoutPrograms)
                .totalActiveStudents(studentsActives)
                .build();
    }

    @Transactional
    public StudentDashboardResponse getStudentDashboard(Long userId) {
        // 1. programas do aluno
        // 2. próximos treinos da semana
        // 3. ficha do dia atual
        String today = LocalDate.now().getDayOfWeek().name();

        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("Aluno nao encontrado"));

        List<TrainingProgram> trainingPrograms = trainingProgramRepository.findByStudentId(user.getId());

        List<DayOfWeek> nextSevenDays = IntStream.range(0, 7)
                .mapToObj(i -> LocalDate.now().plusDays(i).getDayOfWeek().name())
                .map(DayOfWeek::valueOf)
                .toList();

        List<StudentDashboardResponse.StudentProgramSummary> programs = trainingPrograms.stream()
                .map(program -> {
                    List<TrainingSheet> sheets = program.getTrainingSheets();

                    TrainingSheet todaySheet = sheets.stream()
                            .filter(sheet -> sheet.getWeekdays().stream().anyMatch(day -> day.name().equals(today))
                    ).findFirst().orElse(null);

                    List<TrainingSheetResponse> nextTrainings = sheets.stream().filter(sheet -> sheet.getWeekdays()
                            .stream().anyMatch(nextSevenDays::contains))
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
