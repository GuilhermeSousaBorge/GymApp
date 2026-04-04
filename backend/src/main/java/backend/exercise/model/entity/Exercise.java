package backend.exercise.model.entity;

import backend.exercise.model.interfaces.ExerciseUpdatable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "exercises")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 150)
    private String equipment;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ExerciseCategory category;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateFrom(ExerciseUpdatable updatedExercise) {
        if (updatedExercise.getName() != null) {
            this.name = updatedExercise.getName();
        }
        if (updatedExercise.getDescription() != null) {
            this.description = updatedExercise.getDescription();
        }
        if (updatedExercise.getEquipment() != null) {
            this.equipment = updatedExercise.getEquipment();
        }
        if (updatedExercise.getVideoUrl() != null) {
            this.videoUrl = updatedExercise.getVideoUrl();
        }
        if (updatedExercise.getActive() != null) {
            this.active = updatedExercise.getActive();
        }
    }

}
