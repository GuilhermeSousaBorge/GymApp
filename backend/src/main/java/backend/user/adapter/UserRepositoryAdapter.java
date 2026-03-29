package backend.user.adapter;

import backend.user.model.entity.User;
import backend.user.model.valueObjects.Cpf;
import backend.user.model.valueObjects.Email;
import backend.user.port.UserCommandPort;
import backend.user.port.UserQueryPort;
import backend.user.port.UserValidationPort;
import backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR: UserRepository → Ports
 *
 * Implementa as portas (interfaces) definidas pela camada de aplicação.
 * Atua como "bridge" entre a camada de dados (UserRepository - JPA)
 * e a camada de aplicação (Services/UseCases).
 *
 * Benefício: Services não conhecem UserRepository concreto.
 * Se mudar de JPA para MongoDB, apenas alterar este adaptador.
 *
 * Padrão Adapter (Gang of Four):
 * - Adapts: UserRepository (interface Spring Data JPA)
 * - Para: UserQueryPort, UserCommandPort, UserValidationPort
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryAdapter implements UserQueryPort, UserCommandPort, UserValidationPort {
    
    private final UserRepository userRepository;
    
    // ========== UserQueryPort Implementation ==========
    
    @Override
    public Optional<User> findById(Long id) {
        log.debug("UserRepositoryAdapter: findById({})", id);
        return userRepository.findById(id);
    }
    
    @Override
    public Optional<User> findByEmail(Email email) {
        log.debug("UserRepositoryAdapter: findByEmail({})", email.getValue());
        return userRepository.findByEmail(email);
    }
    
    @Override
    public List<User> findAll() {
        log.debug("UserRepositoryAdapter: findAll()");
        return userRepository.findAll();
    }
    
    @Override
    public List<User> findByRole(String roleName) {
        log.debug("UserRepositoryAdapter: findByRole({})", roleName);
        return userRepository.findByRole(roleName);
    }
    
    @Override
    public int countActive() {
        log.debug("UserRepositoryAdapter: countActive()");
        return userRepository.countByActiveTrue();
    }
    
    @Override
    public int countCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("UserRepositoryAdapter: countCreatedBetween({}, {})", startDate, endDate);
        return userRepository.countByCreatedAtBetween(startDate, endDate);
    }
    
    // ========== UserCommandPort Implementation ==========
    
    @Override
    public User save(User user) {
        log.debug("UserRepositoryAdapter: save({})", user.getId());
        return userRepository.save(user);
    }
    
    @Override
    public User update(User user) {
        log.debug("UserRepositoryAdapter: update({})", user.getId());
        return userRepository.save(user); // JPA merge implícito
    }
    
    @Override
    public void deleteById(Long id) {
        log.debug("UserRepositoryAdapter: deleteById({})", id);
        userRepository.deleteById(id);
    }
    
    @Override
    public void setActive(Long id, boolean active) {
        log.debug("UserRepositoryAdapter: setActive({}, {})", id, active);
        userRepository.findById(id).ifPresent(user -> {
            user.setActive(active);
            userRepository.save(user);
        });
    }
    
    // ========== UserValidationPort Implementation ==========
    
    @Override
    public boolean existsByEmail(Email email) {
        log.debug("UserRepositoryAdapter: existsByEmail({})", email.getValue());
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(Email email, Long id) {
        log.debug("UserRepositoryAdapter: existsByEmailAndIdNot({}, {})", email.getValue(), id);
        return userRepository.existsByEmailAndIdNot(email, id);
    }
    
    @Override
    public boolean existsByCpf(Cpf cpf) {
        log.debug("UserRepositoryAdapter: existsByCpf({})", cpf.getValue());
        return userRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existsByCpfAndIdNot(Cpf cpf, Long id) {
        log.debug("UserRepositoryAdapter: existsByCpfAndIdNot({}, {})", cpf.getValue(), id);
        return userRepository.existsByCpfAndIdNot(cpf, id);
    }
    
    @Override
    public long countStudentsWithoutProgram() {
        log.debug("UserRepositoryAdapter: countStudentsWithoutProgram()");
        return userRepository.countStudentsWithoutProgram();
    }
}

