package backend.training.model.entity;

import backend.training.dto.TrainingSheetUpdateRequest;
import backend.training.model.enums.DayOfWeek;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "training_sheets")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingSheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, max = 150, message = "O nome da ficha de treino deve ter entre 1 e 150 caracteres")
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "rest_time_seconds")
    private Integer restTimeSeconds;

    @Column(nullable = false, name = "order_in_program")
    private Integer orderInProgram;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "training_sheet_days", joinColumns = @JoinColumn(name = "training_sheet_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    @Builder.Default
    private Set<DayOfWeek> weekdays = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_program_id", nullable = false)
    private TrainingProgram trainingProgram;

    /**
     * Adicionar uma lista de exercícios aqui, usando @ManyToMany ou @OneToMany dependendo do modelo de dados
     */
    @OneToMany(mappedBy = "trainingSheet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrainingExercise> trainingExercises = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public void updateFrom(TrainingSheetUpdateRequest trainingSheetUpdateRequest) {
        if (trainingSheetUpdateRequest.getName() != null) {
            this.name = trainingSheetUpdateRequest.getName();
        }
        if (trainingSheetUpdateRequest.getDescription() != null) {
            this.description = trainingSheetUpdateRequest.getDescription();
        }
        if (trainingSheetUpdateRequest.getRestTimeSeconds() != null) {
            this.restTimeSeconds = trainingSheetUpdateRequest.getRestTimeSeconds();
        }
        if (trainingSheetUpdateRequest.getOrderInProgram() != null) {
            this.orderInProgram = trainingSheetUpdateRequest.getOrderInProgram();
        }
        if (trainingSheetUpdateRequest.getWeekdays() != null) {
            this.weekdays = trainingSheetUpdateRequest.getWeekdays();
        }
        if (trainingSheetUpdateRequest.getActive() != null) {
            this.active = trainingSheetUpdateRequest.getActive();
        }
    }
}
