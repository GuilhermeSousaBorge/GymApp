package backend.subscription.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.mapper.SubscriptionMapper;
import backend.subscription.model.entity.Subscription;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetActiveSubscriptionByUserUseCaseTest {

    @Mock
    private SubscriptionQueryPort queryPort;

    @Mock
    private SubscriptionMapper mapper;

    @InjectMocks
    private GetActiveSubscriptionByUserUseCase useCase;

    @Test
    void executeShouldReturnActiveSubscription() {
        Subscription subscription = Subscription.builder().id(10L).build();
        SubscriptionResponse response = SubscriptionResponse.builder().id(10L).build();
        Authentication authentication = authWith(2L);

        when(queryPort.findActiveByUserId(2L)).thenReturn(Optional.of(subscription));
        when(mapper.toResponse(subscription)).thenReturn(response);

        SubscriptionResponse result = useCase.execute(2L, authentication);

        assertSame(response, result);
    }

    @Test
    void executeShouldReturnActiveSubscriptionForPrivilegedRole() {
        Subscription subscription = Subscription.builder().id(11L).build();
        SubscriptionResponse response = SubscriptionResponse.builder().id(11L).build();
        Authentication authentication = authWithRole(99L, "ROLE_Administrador");

        when(queryPort.findActiveByUserId(2L)).thenReturn(Optional.of(subscription));
        when(mapper.toResponse(subscription)).thenReturn(response);

        SubscriptionResponse result = useCase.execute(2L, authentication);

        assertSame(response, result);
    }

    @Test
    void executeShouldThrowWhenAuthenticatedUserIsNotOwnerAndNotPrivileged() {
        Authentication authentication = authWith(9L);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> useCase.execute(2L, authentication));

        assertEquals("Sem permissao para consultar assinatura de outro usuario", ex.getMessage());
    }

    @Test
    void executeShouldThrowWhenUserHasNoActiveSubscription() {
        Authentication authentication = authWith(2L);
        when(queryPort.findActiveByUserId(2L)).thenReturn(Optional.empty());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> useCase.execute(2L, authentication));

        assertEquals("Usuario nao possui assinatura ativa", ex.getMessage());
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
