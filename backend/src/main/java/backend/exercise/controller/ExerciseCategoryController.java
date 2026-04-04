package backend.exercise.controller;

import backend.exercise.dto.ExerciseCategoryRequest;
import backend.exercise.dto.ExerciseCategoryUpdateRequest;
import backend.exercise.dto.ExerciseCategoryResponse;
import backend.exercise.usecase.*;
import backend.infrastructure.security.IsAdmin;
import backend.infrastructure.security.IsAdminOrTrainer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER - Exercise Category
 *
 * Endpoints para gerenciar categorias de exercícios
 *
 * Base: /api/exercise-categories
 */
@RestController
@RequestMapping("/api/exercise-categories")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class ExerciseCategoryController {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final ListActiveCategoriesUseCase listActiveCategoriesUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    /**
     * POST /api/exercise-categories
     * Criar nova categoria
     */
    @PostMapping
    @IsAdminOrTrainer
    public ResponseEntity<ExerciseCategoryResponse> createCategory(
            @Valid @RequestBody ExerciseCategoryRequest request) {

        log.info("POST /api/exercise-categories - Grupo muscular: {}", request.getMuscleGroup());

        ExerciseCategoryResponse response = createCategoryUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/exercise-categories/{id}
     * Buscar categoria por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseCategoryResponse> getCategoryById(@PathVariable Long id) {
        log.info("GET /api/exercise-categories/{}", id);

        ExerciseCategoryResponse response = getCategoryByIdUseCase.execute(id);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/exercise-categories
     * Listar todas as categorias
     */
    @GetMapping
    public ResponseEntity<List<ExerciseCategoryResponse>> listCategories(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

        log.info("GET /api/exercise-categories - activeOnly: {}", activeOnly);

        List<ExerciseCategoryResponse> response = activeOnly
                ? listActiveCategoriesUseCase.execute()
                : listCategoriesUseCase.execute();

        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/exercise-categories/{id}
     * Atualizar categoria
     */
    @PutMapping("/{id}")
    @IsAdminOrTrainer
    public ResponseEntity<ExerciseCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody ExerciseCategoryUpdateRequest request) {

        log.info("PUT /api/exercise-categories/{}", id);

        ExerciseCategoryResponse response = updateCategoryUseCase.execute(id, request);

        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/exercise-categories/{id}
     * Deletar permanentemente (apenas admin)
     */
    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("DELETE /api/exercise-categories/{}", id);

        deleteCategoryUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}
