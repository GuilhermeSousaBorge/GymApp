package backend.dashboard.usecase;

import backend.dashboard.dto.AdminDashboardResponse;
import backend.training.port.TrainingProgramQueryPort;
import backend.user.port.UserQueryPort;
import backend.user.port.UserValidationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetAdminDashboardUseCase {

    private final UserQueryPort userQueryPort;
    private final UserValidationPort userValidationPort;
    private final TrainingProgramQueryPort trainingProgramQueryPort;

    @Transactional(readOnly = true)
    public AdminDashboardResponse execute(){
        log.info("Buscando dashboard administrativo");
        // 1. total de alunos ativos
        // 2. total de programas ativos
        // 3. novos alunos no mês
        // 4. alunos sem programa de treino
        LocalDate today = LocalDate.now();

        int studentsActives = userQueryPort.countActive();
        int trainingProgramsActives = trainingProgramQueryPort.countActive();
        long withoutPrograms = userValidationPort.countStudentsWithoutProgram();
        int studentsThisMonth = userQueryPort
                .countCreatedBetween(today.withDayOfMonth(1).atStartOfDay(),
                        today.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59));

        return AdminDashboardResponse.builder()
                .newStudentsThisMonth(studentsThisMonth)
                .totalActivePrograms(trainingProgramsActives)
                .studentsWithoutProgram(withoutPrograms)
                .totalActiveStudents(studentsActives)
                .build();
    }
}
