package backend.exercise.model.entity;

import backend.exercise.model.interfaces.ExerciseCategoryUpdatable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercise_categories")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "muscle_group", nullable = false, length = 100, unique = true)
    private String muscleGroup;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Exercise> exercises = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateFrom(ExerciseCategoryUpdatable updatedCategory) {
        if (updatedCategory.getMuscleGroup() != null) {
            this.muscleGroup = updatedCategory.getMuscleGroup();
        }
        if (updatedCategory.getDescription() != null) {
            this.description = updatedCategory.getDescription();
        }
        if(updatedCategory.getActive() != null) {
            this.active = updatedCategory.getActive();
        }
    }
}
