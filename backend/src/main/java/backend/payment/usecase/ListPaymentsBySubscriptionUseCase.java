package backend.payment.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.payment.dto.PaymentResponse;
import backend.payment.mapper.PaymentMapper;
import backend.payment.port.PaymentQueryPort;
import backend.subscription.model.entity.Subscription;
import backend.subscription.port.SubscriptionQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListPaymentsBySubscriptionUseCase {

    private final SubscriptionQueryPort subscriptionQueryPort;
    private final PaymentQueryPort queryPort;
    private final PaymentMapper mapper;

    @Transactional(readOnly = true)
    public List<PaymentResponse> execute(Long subscriptionId, Authentication authentication) {
        log.info("Listando pagamentos da assinatura {}", subscriptionId);

        if (!(authentication.getPrincipal() instanceof Long authenticatedUserId)) {
            throw new AccessDeniedException("Usuario autenticado invalido");
        }

        Subscription subscription = subscriptionQueryPort.findById(subscriptionId)
                .orElseThrow(() -> new BadRequestException("Assinatura nao encontrada"));

        Long ownerUserId = subscription.getUser().getId();
        if (!authenticatedUserId.equals(ownerUserId) && !isPrivileged(authentication)) {
            throw new AccessDeniedException("Sem permissao para consultar pagamentos desta assinatura");
        }

        return queryPort.findBySubscriptionId(subscriptionId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    private boolean isPrivileged(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> "ROLE_Administrador".equals(authority)
                        || "ROLE_PersonalTrainer".equals(authority));
    }

}

