package backend.subscription.controller;

import backend.infrastructure.exception.UnauthorizedException;
import backend.infrastructure.security.IsAdminOrTrainer;
import backend.subscription.dto.CreateSubscriptionRequest;
import backend.subscription.dto.SubscriptionResponse;
import backend.subscription.model.enums.SubscriptionStatus;
import backend.subscription.usecase.CancelSubscriptionUseCase;
import backend.subscription.usecase.CreateSubscriptionUseCase;
import backend.subscription.usecase.ListByStatusUseCase;
import backend.subscription.usecase.GetActiveSubscriptionByUserUseCase;
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
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class SubscriptionController {

    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;
    private final GetActiveSubscriptionByUserUseCase getActiveSubscriptionByUserUseCase;
    private final ListByStatusUseCase listByStatusUseCase;

    @PostMapping
    @IsAdminOrTrainer
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest request) {
        log.info("POST /api/subscriptions - userId={}, planId={}", request.getUserId(), request.getPlanId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createSubscriptionUseCase.execute(request));
    }

    @PatchMapping("/{id}/cancel")
    @IsAdminOrTrainer
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        log.info("PATCH /api/subscriptions/{}/cancel", id);
        cancelSubscriptionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/active")
    public ResponseEntity<SubscriptionResponse> getActiveByUser(@PathVariable Long userId,
                                                                Authentication authentication) {
        log.info("GET /api/subscriptions/users/{}/active", userId);
        return ResponseEntity.ok(getActiveSubscriptionByUserUseCase.execute(userId, authentication));
    }

    @GetMapping("/me/active")
    public ResponseEntity<SubscriptionResponse> getMyActive(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Long userId)) {
            throw new UnauthorizedException("Usuario nao autenticado");
        }
        log.info("GET /api/subscriptions/me/active - userId={}", userId);
        return ResponseEntity.ok(getActiveSubscriptionByUserUseCase.execute(userId, authentication));
    }

    @GetMapping
    @IsAdminOrTrainer
    public ResponseEntity<List<SubscriptionResponse>> listByStatus(
            @RequestParam (required = false) SubscriptionStatus status){
        log.info("GET /api/subscriptions?status={}", status);

        return ResponseEntity.ok(listByStatusUseCase.execute(status));
    }
}

