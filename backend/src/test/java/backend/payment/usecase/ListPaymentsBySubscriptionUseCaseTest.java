package backend.payment.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.payment.dto.PaymentResponse;
import backend.payment.mapper.PaymentMapper;
import backend.payment.model.entity.Payment;
import backend.payment.port.PaymentQueryPort;
import backend.subscription.model.entity.Subscription;
import backend.user.model.entity.User;
import backend.subscription.port.SubscriptionQueryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPaymentsBySubscriptionUseCaseTest {

    @Mock
    private SubscriptionQueryPort subscriptionQueryPort;

    @Mock
    private PaymentQueryPort queryPort;

    @Mock
    private PaymentMapper mapper;

    @InjectMocks
    private ListPaymentsBySubscriptionUseCase useCase;

    @Test
    void executeShouldListPaymentsWhenSubscriptionExists() {
        Payment payment = Payment.builder().id(1L).build();
        PaymentResponse response = PaymentResponse.builder().id(1L).build();
        Subscription subscription = Subscription.builder().id(1L).user(User.builder().id(7L).build()).build();
        Authentication authentication = authWith(7L);

        when(subscriptionQueryPort.findById(1L)).thenReturn(Optional.of(subscription));
        when(queryPort.findBySubscriptionId(1L)).thenReturn(List.of(payment));
        when(mapper.toResponse(payment)).thenReturn(response);

        List<PaymentResponse> result = useCase.execute(1L, authentication);

        assertEquals(1, result.size());
    }

    @Test
    void executeShouldListPaymentsForPrivilegedRole() {
        Payment payment = Payment.builder().id(2L).build();
        PaymentResponse response = PaymentResponse.builder().id(2L).build();
        Subscription subscription = Subscription.builder().id(1L).user(User.builder().id(7L).build()).build();
        Authentication authentication = authWithRole(99L, "ROLE_PersonalTrainer");

        when(subscriptionQueryPort.findById(1L)).thenReturn(Optional.of(subscription));
        when(queryPort.findBySubscriptionId(1L)).thenReturn(List.of(payment));
        when(mapper.toResponse(payment)).thenReturn(response);

        List<PaymentResponse> result = useCase.execute(1L, authentication);

        assertEquals(1, result.size());
    }

    @Test
    void executeShouldThrowWhenUserIsNotOwnerAndNotPrivileged() {
        Subscription subscription = Subscription.builder().id(1L).user(User.builder().id(7L).build()).build();
        Authentication authentication = authWith(9L);

        when(subscriptionQueryPort.findById(1L)).thenReturn(Optional.of(subscription));

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> useCase.execute(1L, authentication));

        assertEquals("Sem permissao para consultar pagamentos desta assinatura", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenSubscriptionNotFound() {
        Authentication authentication = authWith(7L);
        when(subscriptionQueryPort.findById(99L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> useCase.execute(99L, authentication));

        assertEquals("Assinatura nao encontrada", ex.getMessage());
    }

    private Authentication authWith(Long userId) {
        return new UsernamePasswordAuthenticationToken(userId, "token", List.of());
    }

    private Authentication authWithRole(Long userId, String role) {
        return new UsernamePasswordAuthenticationToken(
                userId,
                "token",
                List.of(new SimpleGrantedAuthority(role))
        );
    }
}
