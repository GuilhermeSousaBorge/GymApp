package backend.controller;

import backend.dto.request.training.TrainingProgramRequest;
import backend.dto.request.training.TrainingProgramUpdateRequest;
import backend.dto.response.training.TrainingProgramResponse;
import backend.infrastructure.security.ProgramOwnerOrAdmin;
import backend.service.TrainingProgramService;
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

    private final TrainingProgramService trainingProgramService;

    @PostMapping
    public ResponseEntity<TrainingProgramResponse> createProgram(
            @Valid @RequestBody TrainingProgramRequest request) {  // ⭐ Opcional!

        log.info("POST /api/training-programs - Student: {}",
                request.getUserId());

        TrainingProgramResponse response = trainingProgramService.createTrainingProgram(request);

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

        TrainingProgramResponse response = trainingProgramService.getTrainingProgramById(programId);

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

        List<TrainingProgramResponse> response = trainingProgramService.listTrainingPrograms(userId);

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

        TrainingProgramResponse response = trainingProgramService.updateTrainingPrograms(programId, request);

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

        trainingProgramService.deactivateProgram(programId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{programId}/activate")
    @ProgramOwnerOrAdmin
    public ResponseEntity<Void> activateProgram(@PathVariable Long programId) {
        log.info("PATCH /api/training-programs/{}/activate", programId);

        trainingProgramService.activateProgram(programId);

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

        trainingProgramService.deleteProgram(programId);

        return ResponseEntity.noContent().build();
    }
}
