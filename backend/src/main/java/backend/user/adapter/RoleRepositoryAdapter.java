package backend.user.adapter;

import backend.user.model.entity.Role;
import backend.user.model.enums.Roles;
import backend.user.port.RolePort;
import backend.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ADAPTADOR: RoleRepository → RolePort
 *
 * Implementa a porta RolePort, permitindo que Services
 * dependam da abstração, não da implementação.
 *
 * Padrão Adapter (Gang of Four)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryAdapter implements RolePort {
    
    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> findById(Long id) {
        log.debug("RoleRepositoryAdapter: findById({})", id);
        return roleRepository.findById(id);
    }
    
    @Override
    public Optional<Role> findByName(Roles role) {
        log.debug("RoleRepositoryAdapter: findByName({})", role.getRole());
        return roleRepository.findByName(role.getRole());
    }
    
    @Override
    public Optional<Role> findByName(String name) {
        log.debug("RoleRepositoryAdapter: findByName({})", name);
        return roleRepository.findByName(name);
    }
}

