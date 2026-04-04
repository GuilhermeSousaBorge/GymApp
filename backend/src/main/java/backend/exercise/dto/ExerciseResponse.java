package backend.exercise.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseResponse {

    private Long id;

    private String name;

    private String description;

    private String equipment;

    private String videoUrl;

    private Boolean active;

    private Long categoryId;

    private CategoryInfo category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CategoryInfo {
        private Long id;
        private String muscleGroup;
        private String description;
        private Boolean active;
    }
}
