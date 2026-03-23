package backend.training.dto;

import backend.exercise.model.entity.Exercise;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingExerciseResponse {

    private Long id;
    private String reps;
    private Integer sets;
    private Integer orderInSheet;
    private Integer restTimeInSeconds;
    private String techniqueNotes;

    private Long exerciseId;

    private Long trainingSheetId;

    private ExerciseInfo exerciseInfo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseInfo {
        private Long id;
        private String name;
        private String description;
        private String equipment;
        private CategoryInfo category;

        @Getter @Setter
        @NoArgsConstructor @AllArgsConstructor
        @Builder
        public static class CategoryInfo {
            private Long id;
            private String muscleGroup;
        }

        public ExerciseInfo(Exercise exercise) {
            this.id = exercise.getId();
            this.name = exercise.getName();
            this.description = exercise.getDescription();
            this.equipment = exercise.getEquipment();
        }
    }
}
