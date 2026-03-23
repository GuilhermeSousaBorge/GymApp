package backend.training.model.entity;

import backend.training.dto.TrainingExerciseUpdateRequest;
import backend.exercise.model.entity.Exercise;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_exercises")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String reps;

    @Column(nullable = false)
    private Integer sets;

    @Column(name = "order_in_sheet")
    private Integer orderInSheet;

    @Column(name = "rest_time_in_seconds")
    private Integer restTimeInSeconds;

    @Column(name = "technique_notes")
    private String techniqueNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_sheet_id", nullable = false)
    private TrainingSheet trainingSheet;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateFrom(TrainingExerciseUpdateRequest updated) {
        if (updated.getReps() != null) {
            this.reps = updated.getReps();
        }
        if (updated.getSets() != null) {
            this.sets = updated.getSets();
        }
        if (updated.getOrderInSheet() != null) {
            this.orderInSheet = updated.getOrderInSheet();
        }
        if (updated.getRestTimeInSeconds() != null) {
            this.restTimeInSeconds = updated.getRestTimeInSeconds();
        }
        if (updated.getTechniqueNotes() != null) {
            this.techniqueNotes = updated.getTechniqueNotes();
        }
    }
}
