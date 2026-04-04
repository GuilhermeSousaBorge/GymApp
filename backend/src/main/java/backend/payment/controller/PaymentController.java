package backend.payment.controller;

import backend.infrastructure.security.IsAdminOrTrainer;
import backend.payment.dto.CreatePaymentRequest;
import backend.payment.dto.PaymentResponse;
import backend.payment.model.enums.PaymentStatus;
import backend.payment.usecase.CreatePaymentUseCase;
import backend.payment.usecase.ListPaymentsByStatusUseCase;
import backend.payment.usecase.ListPaymentsBySubscriptionUseCase;
import backend.payment.usecase.MarkPaymentAsPaidUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final MarkPaymentAsPaidUseCase markPaymentAsPaidUseCase;
    private final ListPaymentsBySubscriptionUseCase listPaymentsBySubscriptionUseCase;
    private final ListPaymentsByStatusUseCase listPaymentsByStatusUseCase;

    @PostMapping
    @IsAdminOrTrainer
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        log.info("POST /api/payments - subscriptionId={}", request.getSubscriptionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createPaymentUseCase.execute(request));
    }

    @PatchMapping("/{id}/pay")
    @IsAdminOrTrainer
    public ResponseEntity<Void> markAsPaid(@PathVariable Long id) {
        log.info("PATCH /api/payments/{}/pay", id);
        markPaymentAsPaidUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/subscriptions/{subscriptionId}")
    public ResponseEntity<List<PaymentResponse>> listBySubscription(@PathVariable Long subscriptionId,
                                                                    Authentication authentication) {
        log.info("GET /api/payments/subscriptions/{}", subscriptionId);
        return ResponseEntity.ok(listPaymentsBySubscriptionUseCase.execute(subscriptionId, authentication));
    }

    @GetMapping
    @IsAdminOrTrainer
    public ResponseEntity<List<PaymentResponse>> listByStatus(
            @RequestParam (required = false) PaymentStatus status){
        log.info("GET /api/payments?status={}", status);

        return ResponseEntity.ok(listPaymentsByStatusUseCase.execute(status));
    }
}

