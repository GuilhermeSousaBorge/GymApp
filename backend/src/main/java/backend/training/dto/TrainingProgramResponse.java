package backend.training.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingProgramResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean active;

    private Userinfo student;

    private Userinfo trainer;

    private Long trainerId;

    private Long userId;

    private List<TrainingSheetInfo> trainingSheets;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Userinfo {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TrainingSheetInfo {
        private Long id;
        private String name;
        private String description;
        private Integer orderInProgram;
        private boolean active;
    }
}
