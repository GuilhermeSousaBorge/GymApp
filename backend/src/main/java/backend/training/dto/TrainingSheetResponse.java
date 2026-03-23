package backend.training.dto;

import backend.training.model.enums.DayOfWeek;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingSheetResponse {

    private Long id;

    private String name;

    private String description;

    private Boolean active;

    private Integer restTimeSeconds;

    private Integer orderInProgram;

    private Set<DayOfWeek> weekdays = new HashSet<>();

    private Long trainingProgramId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
