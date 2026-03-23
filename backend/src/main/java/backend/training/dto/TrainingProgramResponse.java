package backend.training.dto;

import backend.training.model.entity.TrainingSheet;
import backend.user.model.entity.User;
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

        public Userinfo(User user) {
            this.id = user.getId();
            this.name = user.getName();
        }
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

        public TrainingSheetInfo(TrainingSheet trainingSheet) {
            this.id = trainingSheet.getId();
            this.name = trainingSheet.getName();
            this.description = trainingSheet.getDescription();
            this.orderInProgram = trainingSheet.getOrderInProgram();
            this.active = trainingSheet.getActive();
        }
    }
}
