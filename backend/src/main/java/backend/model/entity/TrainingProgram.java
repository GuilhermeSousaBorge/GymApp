package backend.model.entity;

import backend.dto.request.training.TrainingProgramUpdateRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "training_programs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 150, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private User trainer;

    @OneToMany(mappedBy = "trainingProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrainingSheet> trainingSheets = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateFrom(TrainingProgramUpdateRequest request) {
        if (request.getName() != null) this.name = request.getName();
        if (request.getDescription() != null) this.description = request.getDescription();
    }
}
