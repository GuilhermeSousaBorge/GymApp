package backend.subscription.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.mapper.SubscriptionMapper;
import backend.subscription.model.entity.Subscription;
import backend.subscription.port.SubscriptionQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetActiveSubscriptionByUserUseCase {

    private final SubscriptionQueryPort queryPort;
    private final SubscriptionMapper mapper;

    @Transactional(readOnly = true)
    public SubscriptionResponse execute(Long userId, Authentication authentication) {
        log.info("Buscando assinatura ativa do usuario {}", userId);

        if (!(authentication.getPrincipal() instanceof Long authenticatedUserId)) {
            throw new AccessDeniedException("Usuario autenticado invalido");
        }

        if (!authenticatedUserId.equals(userId) && !isPrivileged(authentication)) {
            throw new AccessDeniedException("Sem permissao para consultar assinatura de outro usuario");
        }

        Subscription subscription = queryPort.findActiveByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Usuario nao possui assinatura ativa"));

        return mapper.toResponse(subscription);
    }

    private boolean isPrivileged(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> "ROLE_Administrador".equals(authority)
                        || "ROLE_PersonalTrainer".equals(authority));
    }
}

