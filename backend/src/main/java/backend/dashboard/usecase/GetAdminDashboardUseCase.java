package backend.dashboard.usecase;

import backend.dashboard.dto.AdminDashboardResponse;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentQueryPort;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionQueryPort;
import backend.training.port.TrainingProgramQueryPort;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import backend.user.port.UserValidationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetAdminDashboardUseCase {

    private final UserQueryPort userQueryPort;
    private final UserValidationPort userValidationPort;
    private final TrainingProgramQueryPort trainingProgramQueryPort;
    private final PaymentQueryPort paymentQueryPort;
    private final SubscriptionQueryPort subscriptionQueryPort;

    @Transactional(readOnly = true)
    public AdminDashboardResponse execute() {
        log.info("Buscando dashboard administrativo");

        LocalDate today = LocalDate.now();

        // Existing metrics
        int studentsActives = userQueryPort.countActive();
        int trainingProgramsActives = trainingProgramQueryPort.countActive();
        long withoutPrograms = userValidationPort.countStudentsWithoutProgram();
        int studentsThisMonth = userQueryPort
                .countCreatedBetween(today.withDayOfMonth(1).atStartOfDay(),
                        today.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59));

        // Financial metrics
        long pendingPayments = paymentQueryPort.countByStatus(PaymentStatus.PENDING);
        BigDecimal monthlyRevenue = paymentQueryPort.sumAmountByStatusAndPaymentDateBetween(
                PaymentStatus.PAID,
                today.withDayOfMonth(1),
                today.with(TemporalAdjusters.lastDayOfMonth())
        );
        long activeSubscriptions = subscriptionQueryPort.countByStatus(SubscriptionStatus.ACTIVE);

        // Recent students (last 5)
        List<User> latestUsers = userQueryPort.findLatestStudents(5);
        List<AdminDashboardResponse.RecentStudent> recentStudents = latestUsers.stream()
                .map(u -> AdminDashboardResponse.RecentStudent.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail().getValue())
                        .createdAt(u.getCreatedAt())
                        .build())
                .toList();

        // Chart data — students created per month (last 6 months)
        List<AdminDashboardResponse.MonthlyCount> studentsPerMonth = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            int count = userQueryPort.countCreatedBetween(
                    start.atStartOfDay(),
                    end.atTime(23, 59, 59)
            );
            studentsPerMonth.add(AdminDashboardResponse.MonthlyCount.builder()
                    .month(ym.format(formatter))
                    .count(count)
                    .build());
        }

        return AdminDashboardResponse.builder()
                .totalActiveStudents(studentsActives)
                .totalActivePrograms(trainingProgramsActives)
                .newStudentsThisMonth(studentsThisMonth)
                .studentsWithoutProgram(withoutPrograms)
                .pendingPayments(pendingPayments)
                .monthlyRevenue(monthlyRevenue)
                .activeSubscriptions(activeSubscriptions)
                .recentStudents(recentStudents)
                .studentsPerMonth(studentsPerMonth)
                .build();
    }
}
