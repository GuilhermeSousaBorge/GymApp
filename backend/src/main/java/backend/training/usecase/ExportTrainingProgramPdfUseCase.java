package backend.training.usecase;

import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingProgramExportData;
import backend.training.dto.TrainingProgramPdfFileResponse;
import backend.training.model.entity.TrainingExercise;
import backend.training.model.entity.TrainingProgram;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.port.TrainingProgramPdfExporterPort;
import backend.training.port.TrainingProgramQueryPort;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExportTrainingProgramPdfUseCase {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]+");

    private final TrainingProgramQueryPort programQueryPort;
    private final TrainingSheetQueryPort sheetQueryPort;
    private final TrainingExerciseQueryPort exerciseQueryPort;
    private final TrainingProgramPdfExporterPort pdfExporterPort;

    @Transactional(readOnly = true)
    public TrainingProgramPdfFileResponse execute(Long programId) {
        log.info("Exportando treino em PDF para programId={}", programId);

        TrainingProgram program = programQueryPort.findByIdWithSheets(programId)
                .orElseThrow(() -> new BadRequestException("Programa nao encontrado"));

        boolean hasSheets = sheetQueryPort.countByProgramId(programId) > 0;
        List<TrainingExercise> exercises = exerciseQueryPort.findByProgramWithExercise(programId);

        List<TrainingProgramExportData.ExportRow> rows = exercises.stream()
                .map(exercise -> TrainingProgramExportData.ExportRow.builder()
                        .sheetName(exercise.getTrainingSheet() != null ? exercise.getTrainingSheet().getName() : null)
                        .sheetOrder(exercise.getTrainingSheet() != null ? exercise.getTrainingSheet().getOrderInProgram() : null)
                        .exerciseOrder(exercise.getOrderInSheet())
                        .exerciseName(exercise.getExercise() != null ? exercise.getExercise().getName() : null)
                        .muscleGroup(exercise.getExercise() != null && exercise.getExercise().getCategory() != null
                                ? exercise.getExercise().getCategory().getMuscleGroup()
                                : null)
                        .sets(exercise.getSets())
                        .reps(exercise.getReps())
                        .restTimeInSeconds(exercise.getRestTimeInSeconds())
                        .techniqueNotes(exercise.getTechniqueNotes())
                        .equipment(exercise.getExercise() != null ? exercise.getExercise().getEquipment() : null)
                        .build())
                .toList();

        String userName = program.getStudent() != null ? program.getStudent().getName() : "usuario";
        TrainingProgramExportData exportData = TrainingProgramExportData.builder()
                .programId(program.getId())
                .programName(program.getName())
                .userName(userName)
                .rows(rows)
                .emptyMessage(hasSheets
                        ? "Programa possui folhas, mas ainda nao ha exercicios cadastrados"
                        : "Programa sem folhas de treino cadastradas")
                .build();

        byte[] content = pdfExporterPort.generate(exportData);

        return TrainingProgramPdfFileResponse.builder()
                .content(content)
                .contentType("application/pdf")
                .fileName(buildFileName(program.getName(), userName))
                .build();
    }

    private String buildFileName(String programName, String userName) {
        String programSlug = toSlug(programName, "programa");
        String userSlug = toSlug(userName, "usuario");
        return "programa-" + programSlug + "-" + userSlug + ".pdf";
    }

    private String toSlug(String raw, String fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }

        String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD);
        String noAccents = DIACRITICS.matcher(normalized).replaceAll("");
        String slug = NON_ALNUM.matcher(noAccents.toLowerCase(Locale.ROOT)).replaceAll("-")
                .replaceAll("(^-+|-+$)", "");

        return slug.isBlank() ? fallback : slug;
    }
}

