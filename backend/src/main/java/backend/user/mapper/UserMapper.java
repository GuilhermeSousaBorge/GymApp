package backend.user.mapper;


import backend.user.dto.UserResponse;
import backend.user.model.entity.User;
import org.springframework.stereotype.Component;

/**
 * CAMADA: APPLICATION - Mapper
 *
 * Converte entre Entity e DTO
 *
 * POR QUÊ MAPPER?
 * - Isola as entidades JPA da API
 * - Evita expor dados sensíveis (passwordHash)
 * - Controla exatamente o que vai na resposta
 * - Evita circular references do JPA
 *
 * ALTERNATIVA: Usar MapStruct para geração automática
 */
@Component
public class UserMapper {

    /**
     * Converte User Entity → UserResponse DTO
     *
     * @param user entidade do banco
     * @return DTO para resposta da API
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail().getValue())
                .cpf(user.getCpf().getValue())
                .phone(user.getPhone())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .active(user.getActive())
                .role(toRoleInfo(user))
                .address(toAddressInfo(user))
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Converte Role da entidade para RoleInfo do DTO
     */
    private UserResponse.RoleInfo toRoleInfo(User user) {
        if (user.getRole() == null) {
            return null;
        }

        return UserResponse.RoleInfo.builder()
                .id(user.getRole().getId())
                .name(user.getRole().getName())
                .description(user.getRole().getDescription())
                .permissions(user.getRole().getPermissions())
                .build();
    }

    /**
     * Converte Address da entidade para AddressInfo do DTO
     */
    private UserResponse.AddressInfo toAddressInfo(User user) {
        if (user.getAddress() == null) {
            return null;
        }

        return UserResponse.AddressInfo.builder()
                .id(user.getAddress().getId())
                .number(user.getAddress().getNumber())
                .zipCode(user.getAddress().getZipCode())
                .district(user.getAddress().getDistrict())
                .streetName(user.getAddress().getStreetName())
                .city(user.getAddress().getCity())
                .state(user.getAddress().getState())
                .build();
    }
}
