package backend.plan.controller;

import backend.infrastructure.security.IsAdmin;
import backend.plan.dto.PlanRequest;
import backend.plan.dto.PlanResponse;
import backend.plan.dto.PlanUpdateRequest;
import backend.plan.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class PlanController {

    private final CreatePlanUseCase createPlanUseCase;
    private final UpdatePlanUseCase updatePlanUseCase;
    private final DeletePlanUseCase deletePlanUseCase;
    private final GetPlanByIdUseCase getPlanByIdUseCase;
    private final ListPlansUseCase listPlansUseCase;
    private final ActivatePlanUseCase activatePlanUseCase;
    private final DeactivatePlanUseCase deactivatePlanUseCase;

    @PostMapping
    @IsAdmin
    public ResponseEntity<PlanResponse> create(@Valid @RequestBody PlanRequest request) {
        log.info("POST /api/plans - name={}", request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createPlanUseCase.execute(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> getById(@PathVariable Long id) {
        log.info("GET /api/plans/{}", id);
        return ResponseEntity.ok(getPlanByIdUseCase.execute(id));
    }

    @GetMapping
    public ResponseEntity<List<PlanResponse>> list(@RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        log.info("GET /api/plans - activeOnly={}", activeOnly);
        return ResponseEntity.ok(listPlansUseCase.execute(activeOnly));
    }

    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<PlanResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody PlanUpdateRequest request) {
        log.info("PUT /api/plans/{}", id);
        return ResponseEntity.ok(updatePlanUseCase.execute(id, request));
    }

    @PatchMapping("/{id}/activate")
    @IsAdmin
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        log.info("PATCH /api/plans/{}/activate", id);
        activatePlanUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    @IsAdmin
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        log.info("PATCH /api/plans/{}/deactivate", id);
        deactivatePlanUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/plans/{}", id);
        deletePlanUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

