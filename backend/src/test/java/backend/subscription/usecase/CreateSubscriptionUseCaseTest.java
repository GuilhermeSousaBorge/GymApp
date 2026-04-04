package backend.subscription.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.plan.model.entity.Plan;
import backend.plan.model.valueObject.Money;
import backend.plan.policy.PlanPolicy;
import backend.plan.policy.PlanPolicyResolver;
import backend.plan.port.PlanQueryPort;
import backend.subscription.dto.CreateSubscriptionRequest;
import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.mapper.SubscriptionMapper;
import backend.subscription.model.entity.Subscription;
import backend.subscription.port.SubscriptionCommandPort;
import backend.subscription.port.SubscriptionValidationPort;
import backend.user.model.entity.User;
import backend.user.port.UserQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateSubscriptionUseCaseTest {

    @Mock
    private PlanQueryPort planQueryPort;

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private SubscriptionValidationPort validationPort;

    @Mock
    private SubscriptionCommandPort commandPort;

    @Mock
    private SubscriptionMapper mapper;

    @Mock
    private PlanPolicyResolver policyResolver;

    @InjectMocks
    private CreateSubscriptionUseCase useCase;

    @Test
    void executeShouldCreateSubscriptionWhenUserHasNoActiveSubscription() {
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder().planId(1L).userId(2L).build();
        Plan plan = Plan.builder().id(1L).name("Free").active(true).price(new Money(new BigDecimal("0.00"))).build();
        User user = User.builder().id(2L).name("Aluno").build();
        Subscription saved = Subscription.builder().id(10L).plan(plan).user(user).build();
        SubscriptionResponse response = SubscriptionResponse.builder().id(10L).build();

        PlanPolicy policy = new PlanPolicy() {
            @Override public int getMaxStudents() { return 5; }
            @Override public int getMaxPrograms() { return 1; }
            @Override public boolean allowsCustomExercises() { return false; }
            @Override public boolean allowsVideoUrl() { return false; }
            @Override public String getPolicyKey() { return "FREE"; }
        };

        when(validationPort.existsActiveByUserId(2L)).thenReturn(false);
        when(planQueryPort.findById(1L)).thenReturn(Optional.of(plan));
        when(userQueryPort.findById(2L)).thenReturn(Optional.of(user));
        when(policyResolver.resolve(plan)).thenReturn(policy);
        when(commandPort.save(any(Subscription.class))).thenReturn(saved);
        when(mapper.toResponse(saved)).thenReturn(response);

        SubscriptionResponse result = useCase.execute(request);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenUserAlreadyHasActiveSubscription() {
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder().planId(1L).userId(2L).build();
        when(validationPort.existsActiveByUserId(2L)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Usuario ja possui assinatura ativa", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenPlanIsInactive() {
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder().planId(1L).userId(2L).build();
        Plan plan = Plan.builder().id(1L).name("Free").active(false).price(new Money(new BigDecimal("0.00"))).build();

        when(validationPort.existsActiveByUserId(2L)).thenReturn(false);
        when(planQueryPort.findById(1L)).thenReturn(Optional.of(plan));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Plano inativo nao pode receber novas assinaturas", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenUserNotFound() {
        CreateSubscriptionRequest request = CreateSubscriptionRequest.builder().planId(1L).userId(2L).build();
        Plan plan = Plan.builder().id(1L).name("Free").active(true).price(new Money(new BigDecimal("0.00"))).build();

        when(validationPort.existsActiveByUserId(2L)).thenReturn(false);
        when(planQueryPort.findById(1L)).thenReturn(Optional.of(plan));
        when(userQueryPort.findById(2L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(request));

        assertEquals("Usuario nao encontrado", ex.getMessage());
    }
}
