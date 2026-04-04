package backend.dashboard.usecase;

import backend.dashboard.dto.AdminDashboardResponse;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.port.PaymentQueryPort;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.port.SubscriptionQueryPort;
import backend.training.port.TrainingProgramQueryPort;
import backend.user.port.UserQueryPort;
import backend.user.port.UserValidationPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAdminDashboardUseCaseTest {

    @Mock
    private UserQueryPort userQueryPort;

    @Mock
    private UserValidationPort userValidationPort;

    @Mock
    private TrainingProgramQueryPort trainingProgramQueryPort;

    @Mock
    private PaymentQueryPort paymentQueryPort;

    @Mock
    private SubscriptionQueryPort subscriptionQueryPort;

    @InjectMocks
    private GetAdminDashboardUseCase useCase;

    @Test
    void executeShouldAggregateAdminDashboardMetrics() {
        when(userQueryPort.countActive()).thenReturn(12);
        when(trainingProgramQueryPort.countActive()).thenReturn(9);
        when(userValidationPort.countStudentsWithoutProgram()).thenReturn(3L);
        when(userQueryPort.countCreatedBetween(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenReturn(4);
        when(userQueryPort.findLatestStudents(5)).thenReturn(java.util.Collections.emptyList());
        when(paymentQueryPort.countByStatus(PaymentStatus.PENDING)).thenReturn(7L);
        when(paymentQueryPort.sumAmountByStatusAndPaymentDateBetween(
                org.mockito.ArgumentMatchers.eq(PaymentStatus.PAID),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()))
                .thenReturn(java.math.BigDecimal.TEN);
        when(subscriptionQueryPort.countByStatus(SubscriptionStatus.ACTIVE)).thenReturn(8L);

        AdminDashboardResponse result = useCase.execute();

        assertEquals(12L, result.getTotalActiveStudents());
        assertEquals(9L, result.getTotalActivePrograms());
        assertEquals(3L, result.getStudentsWithoutProgram());
        assertEquals(4L, result.getNewStudentsThisMonth());
        assertEquals(7L, result.getPendingPayments());
        assertEquals(8L, result.getActiveSubscriptions());
    }
}

