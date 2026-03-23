package backend.dashboard.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminDashboardResponse {
    private long totalActiveStudents;
    private long totalActivePrograms;
    private long newStudentsThisMonth;
    private long studentsWithoutProgram;
}
