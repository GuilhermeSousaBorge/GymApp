package backend.plan.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer maxStudents;
    private Integer maxPrograms;
    private Set<String> benefits;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

