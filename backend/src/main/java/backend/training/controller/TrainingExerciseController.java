package backend.training.controller;

import backend.training.dto.TrainingExerciseRequest;
import backend.training.dto.TrainingExerciseUpdateRequest;
import backend.training.dto.TrainingExerciseResponse;
import backend.infrastructure.security.ExerciseOwnerOrAdmin;
import backend.infrastructure.security.SheetOwnerOrAdmin;
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
@RequestMapping("/api/training-exercises")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class TrainingExerciseController {

    private final CreateExerciseUseCase createExerciseUseCase;
    private final GetExerciseByIdUseCase getExerciseByIdUseCase;
    private final GetSheetExercisesUseCase getSheetExercisesUseCase;
    private final UpdateExerciseUseCase updateExerciseUseCase;
    private final ReorderExerciseUseCase reorderExerciseUseCase;
    private final DeleteExerciseUseCase deleteExerciseUseCase;

    /**
     * POST /api/training-exercises/{TrainingExerciseId}
     * Adicionar exercício na folha
     */
    @PostMapping
    public ResponseEntity<TrainingExerciseResponse> createTrainingExercise(
            @Valid @RequestBody TrainingExerciseRequest request) {

        log.info("POST /api/training-sheets");

        TrainingExerciseResponse response = createExerciseUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/training-exercises/{id}
     * Buscar exercício por ID
     */
    @GetMapping("/{exerciseId}")
    @ExerciseOwnerOrAdmin
    public ResponseEntity<TrainingExerciseResponse> getExerciseById(@PathVariable Long exerciseId) {
        log.info("GET /api/training-exercises/{}", exerciseId);

        TrainingExerciseResponse response = getExerciseByIdUseCase.execute(exerciseId);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/training-exercises?sheetId={sheetId}
     * Listar exercícios da folha (ordenados)
     */
    @GetMapping
    @SheetOwnerOrAdmin
    public ResponseEntity<List<TrainingExerciseResponse>> getSheetExercises(
            @RequestParam() Long sheetId) {
        log.info("GET /api/training-exercises/{}", sheetId);

        List<TrainingExerciseResponse> response = getSheetExercisesUseCase.execute(sheetId);

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/training-exercises/{id}
     * Atualizar exercício (sets, reps, ordem, etc)
     */
    @PutMapping("/{exerciseId}")
    @ExerciseOwnerOrAdmin
    public ResponseEntity<TrainingExerciseResponse> updateExercise(
            @PathVariable Long exerciseId,
            @Valid @RequestBody TrainingExerciseUpdateRequest request) {

        log.info("PUT /api/training-exercises/{}", exerciseId);

        TrainingExerciseResponse response = updateExerciseUseCase.execute(exerciseId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/training-exercises/{id}/reorder
     * Mudar ordem do exercício
     */
    @PatchMapping("/{exerciseId}/reorder")
    @ExerciseOwnerOrAdmin
    public ResponseEntity<TrainingExerciseResponse> reorderExercise(
            @PathVariable Long exerciseId,
            @RequestParam Integer newOrder) {

        log.info("PATCH /api/training-exercises/{}/reorder - Order: {}", exerciseId, newOrder);

        TrainingExerciseResponse response = reorderExerciseUseCase.execute(exerciseId, newOrder);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/training-exercises/{id}
     * Remover exercício da folha
     */
    @DeleteMapping("/{exerciseId}")
    @ExerciseOwnerOrAdmin
    public ResponseEntity<Void> deleteExercise(@PathVariable Long exerciseId) {
        log.info("DELETE /api/training-exercises/{}", exerciseId);

        deleteExerciseUseCase.execute(exerciseId);

        return ResponseEntity.noContent().build();
    }
}
