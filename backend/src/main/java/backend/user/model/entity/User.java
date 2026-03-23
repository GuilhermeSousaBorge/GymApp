package backend.user.model.entity;

import backend.user.dto.UserRequest;
import backend.user.model.converters.CpfConverter;
import backend.user.model.converters.EmailConverter;
import backend.user.model.converters.PasswordConverter;
import backend.user.model.enums.Gender;
import backend.user.model.interfaces.UserUpdatable;
import backend.user.model.valueObjects.Cpf;
import backend.user.model.valueObjects.Email;
import backend.user.model.valueObjects.Password;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;
import backend.training.model.entity.TrainingProgram;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CAMADA: DOMAIN - Entidade JPA
 *
 * Representa um usuário do sistema (Admin, Personal, Aluno, etc)
 * Mapeia para a tabela "users" no banco de dados
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    @Convert(converter = EmailConverter.class)
    private Email email;

    @Column(name = "password_hash", nullable = false)
    @Convert(converter = PasswordConverter.class)
    private Password passwordHash;  // Senha criptografada com BCrypt

    @Column(unique = true, length = 14)
    @Convert(converter = CpfConverter.class)
    private Cpf cpf;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false)
    @Builder.Default  // Valor padrão no Builder
    private Boolean active = true;

    /**
     * Relacionamento Many-to-One com Role
     * Muitos usuários podem ter a mesma role
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * Relacionamento One-to-One com Address
     * Um usuário tem um endereço
     * CascadeType.ALL: operações em User são propagadas para Address
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(mappedBy = "student")
    @Builder.Default
    private List<TrainingProgram> trainingPrograms = new ArrayList<>();

    @OneToMany(mappedBy = "trainer")
    @Builder.Default
    private List<TrainingProgram> trainerPrograms = new ArrayList<>();

    /**
     * Auditoria automática
     * Timestamps gerenciados pelo Hibernate
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public boolean isPasswordValid(String rawPassword, PasswordEncoder encoder) {
        return this.passwordHash.matches(rawPassword, encoder);
    }

    public void updateForm(UserUpdatable request){
        if(request.getName() != null) this.name = request.getName();
        if(request.getEmail() != null) this.email = new Email(request.getEmail());
        if(request.getCpf() != null) this.cpf = new Cpf(request.getCpf());
        if(request.getPhone() != null) this.phone = request.getPhone();
        if(request.getGender() != null) this.gender = request.getGender();
        if(request.getBirthDate() != null) this.birthDate = request.getBirthDate();
    }
}
