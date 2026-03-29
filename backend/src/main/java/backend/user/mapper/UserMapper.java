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
                .email(user.getEmail().getValue() != null ? user.getEmail().getValue() : null)
                .cpf(user.getCpf().getValue() !=  null ? user.getCpf().getValue() : null)
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

        UserResponse.RoleInfo roleInfo = new UserResponse.RoleInfo();
        roleInfo.setId(user.getRole().getId());
        roleInfo.setName(user.getRole().getName());
        roleInfo.setDescription(user.getRole().getDescription());
        roleInfo.setPermissions(user.getRole().getPermissions());
        return roleInfo;
    }

    /**
     * Converte Address da entidade para AddressInfo do DTO
     */
    private UserResponse.AddressInfo toAddressInfo(User user) {
        if (user.getAddress() == null) {
            return null;
        }

        UserResponse.AddressInfo addressInfo = new UserResponse.AddressInfo();
        addressInfo.setId(user.getAddress().getId());
        addressInfo.setNumber(user.getAddress().getNumber());
        addressInfo.setZipCode(user.getAddress().getZipCode());
        addressInfo.setDistrict(user.getAddress().getDistrict());
        addressInfo.setStreetName(user.getAddress().getStreetName());
        addressInfo.setCity(user.getAddress().getCity());
        addressInfo.setState(user.getAddress().getState());
        return addressInfo;
    }
}
