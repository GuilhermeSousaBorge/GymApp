package backend.plan.model.entity;

import backend.plan.model.converters.MoneyConverter;
import backend.plan.model.valueObject.Money;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * CAMADA: DOMAIN - Entidade JPA
 *
 * Representa um plano de assinatura disponível no sistema.
 * Mapeia para a tabela "plans" no banco de dados.
 *
 * Fase 4 (OCP): ponto de extensão via PlanPolicy — cada tipo de plano
 * define seus próprios limites sem modificar código existente.
 */
@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @Convert(converter = MoneyConverter.class)
    private Money price;

    /**
     * Número máximo de alunos que um personal pode ter neste plano.
     * Usado pela PlanPolicy para validação (OCP).
     */
    @Column(name = "max_students", nullable = false)
    private Integer maxStudents;

    /**
     * Número máximo de programas de treino por aluno neste plano.
     * Usado pela PlanPolicy para validação (OCP).
     */
    @Column(name = "max_programs", nullable = false)
    private Integer maxPrograms;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "plan_benefits", joinColumns = @JoinColumn(name = "plan_id"))
    @Column(name = "benefits",  nullable = false)
    @Builder.Default
    private Set<String> benefits = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}