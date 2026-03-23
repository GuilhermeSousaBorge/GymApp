package backend.exercise.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseUpdateRequest {

    @Size(max = 150)
    private String name;

    private String description;

    @Size(max = 100)
    private String equipment;

    @Size(max = 255)
    private String videoUrl;

//    @Size(max = 255)
//    private String imageUrl;

    private Boolean active;

    private Long categoryId;
}
