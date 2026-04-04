package backend.training.infrastructure.pdf;

import backend.training.dto.TrainingProgramExportData;
import backend.training.dto.TrainingProgramExportData.ExportRow;
import backend.training.port.TrainingProgramPdfExporterPort;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Layout SIMPLES — preto e branco, ideal para impressão.
 */

@Component("pdfExporterSimples")
public class TrainingProgramPdfSimples implements TrainingProgramPdfExporterPort {

    private static final Font FONT_TITLE   = new Font(Font.HELVETICA, 18, Font.BOLD,   Color.BLACK);
    private static final Font FONT_META    = new Font(Font.HELVETICA,  9, Font.NORMAL, Color.DARK_GRAY);
    private static final Font FONT_SECTION = new Font(Font.HELVETICA, 11, Font.BOLD,   Color.BLACK);
    private static final Font FONT_HEADER  = new Font(Font.HELVETICA,  8, Font.BOLD,   Color.DARK_GRAY);
    private static final Font FONT_CELL    = new Font(Font.HELVETICA,  8, Font.NORMAL, Color.BLACK);
    private static final Font FONT_SMALL   = new Font(Font.HELVETICA,  7, Font.NORMAL, Color.GRAY);

    @Override
    public byte[] generate(TrainingProgramExportData data) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 40, 40, 50, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // Cabeçalho
            doc.add(new Paragraph(data.getProgramName(), FONT_TITLE));
            doc.add(new Paragraph("Aluno: " + data.getUserName(), FONT_META));
            doc.add(new Paragraph(
                "Gerado em: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                FONT_SMALL
            ));
            doc.add(Chunk.NEWLINE);

            // Sem exercícios
            if (data.getRows() == null || data.getRows().isEmpty()) {
                doc.add(new Paragraph(data.getEmptyMessage(), FONT_META));
                doc.close();
                return out.toByteArray();
            }

            // Agrupa por ficha (mantendo ordem)
            Map<String, List<ExportRow>> bySheet = groupBySheet(data.getRows());

            for (Map.Entry<String, List<ExportRow>> entry : bySheet.entrySet()) {
                doc.add(new Paragraph(entry.getKey(), FONT_SECTION));
                doc.add(Chunk.NEWLINE);

                PdfPTable table = new PdfPTable(new float[]{3f, 1f, 1.2f, 1.5f, 3f});
                table.setWidthPercentage(100);
                table.setSpacingAfter(12);

                addHeader(table, "Exercício");
                addHeader(table, "Séries");
                addHeader(table, "Reps");
                addHeader(table, "Descanso");
                addHeader(table, "Observações");

                for (ExportRow row : entry.getValue()) {
                    addCell(table, nvl(row.getExerciseName()));
                    addCell(table, row.getSets() != null ? row.getSets() + "x" : "—");
                    addCell(table, nvl(row.getReps()));
                    addCell(table, row.getRestTimeInSeconds() != null ? row.getRestTimeInSeconds() + "s" : "—");
                    addCell(table, nvl(row.getTechniqueNotes()));
                }

                doc.add(table);
            }

            doc.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF simples", e);
        }
    }

    // ── helpers ────────────────────────────────────────────────

    private void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HEADER));
        cell.setBackgroundColor(new Color(220, 220, 220));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_CELL));
        cell.setPadding(4);
        table.addCell(cell);
    }

    private String nvl(String value) {
        return value != null && !value.isBlank() ? value : "—";
    }

    private Map<String, List<ExportRow>> groupBySheet(List<ExportRow> rows) {
        return rows.stream()
            .sorted(Comparator
                .comparingInt((ExportRow r) -> r.getSheetOrder() != null ? r.getSheetOrder() : 0)
                .thenComparingInt(r -> r.getExerciseOrder() != null ? r.getExerciseOrder() : 0))
            .collect(Collectors.groupingBy(
                r -> r.getSheetName() != null ? r.getSheetName() : "Sem ficha",
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }
}
