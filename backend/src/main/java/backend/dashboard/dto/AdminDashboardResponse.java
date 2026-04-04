package backend.dashboard.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardResponse {

    // Existing metrics
    private long totalActiveStudents;
    private long totalActivePrograms;
    private long newStudentsThisMonth;
    private long studentsWithoutProgram;

    // Financial metrics
    private long pendingPayments;
    private BigDecimal monthlyRevenue;
    private long activeSubscriptions;

    // Recent students
    private List<RecentStudent> recentStudents;

    // Chart data — students per month (last 6 months)
    private List<MonthlyCount> studentsPerMonth;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RecentStudent {
        private Long id;
        private String name;
        private String email;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MonthlyCount {
        private String month; // "2026-01", "2026-02", etc.
        private long count;
    }
}
