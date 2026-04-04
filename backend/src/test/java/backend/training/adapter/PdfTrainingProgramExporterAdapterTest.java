package backend.training.adapter;

import backend.training.dto.TrainingProgramExportData;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfTrainingProgramExporterAdapterTest {

    private final PdfTrainingProgramExporterAdapter adapter = new PdfTrainingProgramExporterAdapter();

    @Test
    void generateShouldReturnPdfBytesWhenRowsExist() {
        TrainingProgramExportData data = TrainingProgramExportData.builder()
                .programId(1L)
                .programName("Hipertrofia")
                .userName("Joao")
                .rows(List.of(
                        TrainingProgramExportData.ExportRow.builder()
                                .sheetName("Treino A")
                                .sheetOrder(1)
                                .exerciseOrder(1)
                                .exerciseName("Supino")
                                .muscleGroup("Peito")
                                .sets(4)
                                .reps("10")
                                .restTimeInSeconds(60)
                                .techniqueNotes("Controlado")
                                .equipment("Barra")
                                .build()
                ))
                .emptyMessage("Programa sem folhas de treino cadastradas")
                .build();

        byte[] content = adapter.generate(data);

        assertTrue(content.length > 0);
    }

    @Test
    void generateShouldReturnPdfBytesWhenNoRowsExist() {
        TrainingProgramExportData data = TrainingProgramExportData.builder()
                .programId(2L)
                .programName("Iniciante")
                .userName("Maria")
                .rows(List.of())
                .emptyMessage("Programa sem folhas de treino cadastradas")
                .build();

        byte[] content = adapter.generate(data);

        assertTrue(content.length > 0);
    }
}

