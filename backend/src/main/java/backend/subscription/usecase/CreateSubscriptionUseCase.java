package backend.subscription.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.model.entity.Plan;
import backend.plan.policy.PlanPolicy;
import backend.plan.policy.PlanPolicyResolver;
import backend.plan.port.PlanQueryPort;
import backend.subscription.dto.CreateSubscriptionRequest;
import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.mapper.SubscriptionMapper;
import backend.subscription.model.entity.Subscription;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionCommandPort;
import backend.subscription.port.SubscriptionValidationPort;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateSubscriptionUseCase {

    private final PlanQueryPort planQueryPort;
    private final UserQueryPort userQueryPort;
    private final SubscriptionValidationPort validationPort;
    private final SubscriptionCommandPort commandPort;
    private final SubscriptionMapper mapper;
    private final PlanPolicyResolver policyResolver;

    @Transactional
    public SubscriptionResponse execute(CreateSubscriptionRequest request) {
        log.info("Criando assinatura para userId={} no planId={}", request.getUserId(), request.getPlanId());

        if (validationPort.existsActiveByUserId(request.getUserId())) {
            throw new BadRequestException("Usuario ja possui assinatura ativa");
        }

        Plan plan = planQueryPort.findById(request.getPlanId())
                .orElseThrow(() -> new BadRequestException("Plano nao encontrado"));

        if (!Boolean.TRUE.equals(plan.getActive())) {
            throw new BadRequestException("Plano inativo nao pode receber novas assinaturas");
        }

        User user = userQueryPort.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException("Usuario nao encontrado"));

        PlanPolicy policy = policyResolver.resolve(plan);
        log.info("Politica selecionada para plano {}: {}", plan.getName(), policy.getPolicyKey());

        LocalDateTime startDate = request.getStartDate() != null ? request.getStartDate() : LocalDateTime.now();

        Subscription saved = commandPort.save(Subscription.builder()
                .plan(plan)
                .user(user)
                .startDate(startDate)
                .endDate(request.getEndDate())
                .status(SubscriptionStatus.ACTIVE)
                .autoRenew(request.getAutoRenew() != null ? request.getAutoRenew() : true)
                .planPriceAtStart(plan.getPrice().getValue())
                .build());

        return mapper.toResponse(saved);
    }
}

