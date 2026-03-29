package backend.training.controller;

import backend.training.dto.TrainingProgramRequest;
import backend.training.dto.TrainingProgramUpdateRequest;
import backend.training.dto.TrainingProgramResponse;
import backend.infrastructure.security.ProgramOwnerOrAdmin;
import backend.training.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training-programs")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class TrainingProgramController {

    private final CreateProgramUseCase createProgramUseCase;
    private final GetProgramByIdUseCase getProgramByIdUseCase;
    private final ListProgramUseCase listProgramUseCase;
    private final UpdateProgramUseCase updateProgramUseCase;
    private final ActivateProgramUseCase activateProgramUseCase;
    private final DeactivateProgramUseCase deactivateProgramUseCase;
    private final DeleteProgramUseCase deleteProgramUseCase;

    @PostMapping
    public ResponseEntity<TrainingProgramResponse> createProgram(
            @Valid @RequestBody TrainingProgramRequest request) {  // ⭐ Opcional!

        log.info("POST /api/training-programs - Student: {}",
                request.getUserId());

        TrainingProgramResponse response = createProgramUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/training-programs/{id}
     * Buscar programa por ID
     */
    @GetMapping("/{programId}")
    @ProgramOwnerOrAdmin
    public ResponseEntity<TrainingProgramResponse> getProgramById(@PathVariable Long programId) {
        log.info("GET /api/training-programs/{}", programId);

        TrainingProgramResponse response = getProgramByIdUseCase.execute(programId);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/training-programs
     * Listar todos os programas
     */
    @GetMapping
    @PreAuthorize("hasRole('Administrador') or hasRole('PersonalTrainer') or #userId == authentication.principal")
    public ResponseEntity<List<TrainingProgramResponse>> getAllPrograms(@RequestParam (required = false) Long userId) {
        log.info("GET /api/training-programs/all");

        List<TrainingProgramResponse> response = listProgramUseCase.execute(userId);

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/training-programs/{id}
     * Atualizar programa
     */
    @PutMapping("/{programId}")
    @ProgramOwnerOrAdmin
    public ResponseEntity<TrainingProgramResponse> updateProgram(
            @PathVariable Long programId,
            @Valid @RequestBody TrainingProgramUpdateRequest request) {

        log.info("PUT /api/training-programs/{}", programId);

        TrainingProgramResponse response = updateProgramUseCase.execute(programId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/training-programs/{id}/deactivate
     * Desativar programa
     */
    @PatchMapping("/{programId}/deactivate")
    @ProgramOwnerOrAdmin
    public ResponseEntity<Void> deactivateProgram(@PathVariable Long programId) {
        log.info("PATCH /api/training-programs/{}/deactivate", programId);

        deactivateProgramUseCase.execute(programId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{programId}/activate")
    @ProgramOwnerOrAdmin
    public ResponseEntity<Void> activateProgram(@PathVariable Long programId) {
        log.info("PATCH /api/training-programs/{}/activate", programId);

        activateProgramUseCase.execute(programId);

        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/training-programs/{id}
     * Deletar permanentemente
     */
    @DeleteMapping("/{programId}")
    @ProgramOwnerOrAdmin
    public ResponseEntity<Void> deleteProgram(@PathVariable Long programId) {
        log.info("DELETE /api/training-programs/{}", programId);

        deleteProgramUseCase.execute(programId);

        return ResponseEntity.noContent().build();
    }
}
