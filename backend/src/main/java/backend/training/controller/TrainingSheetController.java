package backend.training.controller;

import backend.training.dto.TrainingSheetRequest;
import backend.training.dto.TrainingSheetUpdateRequest;
import backend.training.dto.TrainingSheetResponse;
import backend.infrastructure.security.ProgramOwnerOrAdmin;
import backend.infrastructure.security.SheetOwnerOrAdmin;
import backend.training.model.enums.DayOfWeek;
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
@RequestMapping("/api/training-sheets")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class TrainingSheetController {

    private final CreateSheetUseCase createSheetUseCase;
    private final GetSheetByIdUseCase getSheetByIdUseCase;
    private final GetSheetByDayOfWeekUseCase getSheetByDayOfWeekUseCase;
    private final GetSheetFromProgramUseCase getSheetFromProgramUseCase;
    private final GetActiveSheetsFromProgramUseCase getActiveSheetsFromProgramUseCase;
    private final UpdateSheetUseCase updateSheetUseCase;
    private final ReorderSheetUseCase reorderSheetUseCase;
    private final ActivateSheetUseCase activateSheetUseCase;
    private final DeactivateSheetUseCase deactivateSheetUseCase;
    private final DeleteSheetUseCase deleteSheetUseCase;

    /**
     * POST /api/training-sheets
     * Criar nova folha de treino
     *
     * Body exemplo:
     * {
     *   "name": "Treino A - Peito e Tríceps",
     *   "description": "Foco em hipertrofia",
     *   "trainingProgramId": 1,
     *   "weekdays": ["MONDAY", "THURSDAY"],
     *   "restTimeSeconds": 60,
     *   "orderInSheet": 1
     * }
     */
    @PostMapping
    public ResponseEntity<TrainingSheetResponse> createSheet(
            @Valid @RequestBody TrainingSheetRequest request) {

        log.info("POST /api/training-sheets - Name: {}, Program: {}",
                request.getName(), request.getTrainingProgramId());

        TrainingSheetResponse response = createSheetUseCase.execute(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/training-sheets/{id}
     * Buscar folha por ID
     */
    @GetMapping("/{sheetId}")
    @SheetOwnerOrAdmin
    public ResponseEntity<TrainingSheetResponse> getSheetById(@PathVariable Long sheetId) {
        log.info("GET /api/training-sheets/{}", sheetId);

        TrainingSheetResponse response = getSheetByIdUseCase.execute(sheetId);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/training-sheets
     * Listar folhas com filtros opcionais
     *
     * Query params:
     * - programId: Filtrar por programa
     * - dayOfWeek: Filtrar por dia (MONDAY, TUESDAY, etc)
     * - activeOnly: true/false (padrão: false)
     *
     * Exemplos:
     * GET /api/training-sheets
     * GET /api/training-sheets?programId=1
     * GET /api/training-sheets?dayOfWeek=MONDAY
     * GET /api/training-sheets?programId=1&activeOnly=true
     */
    @GetMapping
    @ProgramOwnerOrAdmin
    public ResponseEntity<List<TrainingSheetResponse>> listSheets(
            @RequestParam() Long programId,
            @RequestParam(required = false) DayOfWeek dayOfWeek,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

        log.info("GET /api/training-sheets - Program: {}, Day: {}, Active: {}",
                programId, dayOfWeek, activeOnly);

        List<TrainingSheetResponse> response = List.of();

        if (programId != null) {
            // Filtrar por programa
            response = activeOnly
                    ? getActiveSheetsFromProgramUseCase.execute(programId)
                    : getSheetFromProgramUseCase.execute(programId);
        } else if (dayOfWeek != null) {
            // Filtrar por dia da semana
            response = getSheetByDayOfWeekUseCase.execute(dayOfWeek);
        }

        return ResponseEntity.ok(response);
    }


    /**
     * PUT /api/training-sheets/{id}
     * Atualizar folha de treino
     *
     * Body exemplo:
     * {
     *   "name": "Treino A - Peito e Tríceps (Atualizado)",
     *   "description": "Nova descrição",
     *   "weekdays": ["MONDAY", "WEDNESDAY", "FRIDAY"],
     *   "restTimeSeconds": 90,
     *   "orderInSheet": 2
     * }
     */
    @PutMapping("/{sheetId}")
    @SheetOwnerOrAdmin
    public ResponseEntity<TrainingSheetResponse> updateSheet(
            @PathVariable Long sheetId,
            @Valid @RequestBody TrainingSheetUpdateRequest request) {

        log.info("PUT /api/training-sheets/{}", sheetId);

        TrainingSheetResponse response = updateSheetUseCase.execute(sheetId, request);

        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/training-sheets/{id}/reorder
     * Alterar ordem da folha no programa
     *
     * Query param:
     * - newOrder: número inteiro (1, 2, 3...)
     *
     * Exemplo: PATCH /api/training-sheets/1/reorder?newOrder=3
     */
    @PatchMapping("/{sheetId}/reorder")
    @SheetOwnerOrAdmin
    public ResponseEntity<TrainingSheetResponse> reorderSheet(
            @PathVariable Long sheetId,
            @RequestParam Integer newOrder) {

        log.info("PATCH /api/training-sheets/{}/reorder - Order: {}", sheetId, newOrder);

        TrainingSheetResponse response = reorderSheetUseCase.execute(sheetId, newOrder);

        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/training-sheets/{id}/activate
     * Ativar folha
     */
    @PatchMapping("/{sheetId}/activate")
    @SheetOwnerOrAdmin
    public ResponseEntity<Void> activateSheet(@PathVariable Long sheetId) {
        log.info("PATCH /api/training-sheets/{}/activate", sheetId);

        activateSheetUseCase.execute(sheetId);

        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/training-sheets/{id}/deactivate
     * Desativar folha (soft delete)
     */
    @PatchMapping("/{sheetId}/deactivate")
    @SheetOwnerOrAdmin
    public ResponseEntity<Void> deactivateSheet(@PathVariable Long sheetId) {
        log.info("PATCH /api/training-sheets/{}/deactivate", sheetId);

        deactivateSheetUseCase.execute(sheetId);

        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/training-sheets/{id}
     * Deletar folha permanentemente
     */
    @DeleteMapping("/{sheetId}")
    @SheetOwnerOrAdmin
    public ResponseEntity<Void> deleteSheet(@PathVariable Long sheetId) {
        log.info("DELETE /api/training-sheets/{}", sheetId);

        deleteSheetUseCase.execute(sheetId);

        return ResponseEntity.noContent().build();
    }
}
