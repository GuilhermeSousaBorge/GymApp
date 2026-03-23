package backend.dashboard.dto;

import backend.training.dto.TrainingProgramResponse;
import backend.training.dto.TrainingSheetResponse;
import backend.training.model.entity.TrainingProgram;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentDashboardResponse {
    private List<StudentProgramSummary> programs;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StudentProgramSummary {
        private Long id;
        private String name;
        private boolean active;
        private TrainingSheetResponse todaySheet;
        private List<TrainingSheetResponse> nextTrainings;
    }
}
