package backend.exercise.controller;

import backend.exercise.dto.ExerciseRequest;
import backend.exercise.dto.ExerciseUpdateRequest;
import backend.exercise.dto.ExerciseResponse;
import backend.infrastructure.security.IsAdmin;
import backend.infrastructure.security.IsAdminOrTrainer;
import backend.exercise.service.ExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER - Exercise
 *
 * Endpoints para gerenciar exercícios
 *
 * Base: /api/exercises
 */
@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * POST /api/exercises
     * Criar novo exercício
     */
    @PostMapping
    @IsAdminOrTrainer
    public ResponseEntity<ExerciseResponse> createExercise(
            @Valid @RequestBody ExerciseRequest request) {

        log.info("POST /api/exercises - Name: {}", request.getName());

        ExerciseResponse response = exerciseService.createExercise(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/exercises/{id}
     * Buscar exercício por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getExerciseById(@PathVariable Long id) {
        log.info("GET /api/exercises/{}", id);

        ExerciseResponse response = exerciseService.getExerciseById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/exercises
     * Listar todos os exercícios
     */
    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> listExercises(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {

        log.info("GET /api/exercises - activeOnly: {}, categoryId: {}, search: {}",
                activeOnly, categoryId, search);

        List<ExerciseResponse> response;

        if (search != null && !search.isBlank()) {
            // Busca por termo
            response = exerciseService.searchExercises(search);
        } else if (categoryId != null) {
            // Filtro por categoria
            response = exerciseService.getExercisesByCategory(categoryId);
        } else if (activeOnly) {
            // Apenas ativos
            response = exerciseService.listActiveExercises();
        } else {
            // Todos
            response = exerciseService.listAllExercises();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/exercises/{id}
     * Atualizar exercício
     */
    @PutMapping("/{id}")
    @IsAdminOrTrainer
    public ResponseEntity<ExerciseResponse> updateExercise(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseUpdateRequest request) {

        log.info("PUT /api/exercises/{}", id);

        ExerciseResponse response = exerciseService.updateExercise(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/exercises/{id}
     * Deletar permanentemente (apenas admin)
     */
    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        log.info("DELETE /api/exercises/{}", id);

        exerciseService.deleteExercise(id);

        return ResponseEntity.noContent().build();
    }
}
