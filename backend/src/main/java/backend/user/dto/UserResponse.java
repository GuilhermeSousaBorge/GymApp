package backend.user.dto;

import backend.user.model.enums.Gender;
import backend.user.model.enums.Permission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * CAMADA: APPLICATION - DTO Response
 *
 * Representa os dados do usuário que serão retornados pela API
 *
 * IMPORTANTE:
 * - NÃO inclui passwordHash (segurança)
 * - Inclui apenas os dados necessários para o frontend
 * - Evita circular references de entidades JPA
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private Gender gender;
    private LocalDate birthDate;
    private Boolean active;

    // Role simplificada (sem nested objects profundos)
    private RoleInfo role;

    // Address simplificado
    private AddressInfo address;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Classe interna para representar Role de forma simplificada
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoleInfo {
        private Long id;
        private String name;
        private String description;
        private Set<Permission> permissions;
    }

    /**
     * Classe interna para representar Address de forma simplificada
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressInfo {
        private Long id;
        private Integer number;
        private String zipCode;
        private String district;
        private String streetName;
        private String city;
        private String state;
    }
}
