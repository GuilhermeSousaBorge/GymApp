package backend.training.infrastructure.pdf;

import backend.training.dto.TrainingProgramExportData;
import backend.training.dto.TrainingProgramExportData.ExportRow;
import backend.training.port.TrainingProgramPdfExporterPort;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Layout MEIO-TERMO — cabeçalho azul, linhas alternadas, grupo muscular visível.
 */

@Component("pdfExporterMedium")
public class TrainingProgramPdfMedium implements TrainingProgramPdfExporterPort {

    private static final Color PRIMARY       = new Color(33,  97, 140);
    private static final Color PRIMARY_LIGHT = new Color(214, 234, 248);
    private static final Color ROW_ALT       = new Color(245, 245, 245);
    private static final Color BORDER        = new Color(190, 190, 190);

    private static final Font FONT_PROG_TITLE = new Font(Font.HELVETICA, 20, Font.BOLD,   Color.WHITE);
    private static final Font FONT_PROG_META  = new Font(Font.HELVETICA,  9, Font.NORMAL, Color.WHITE);
    private static final Font FONT_SHEET_NAME = new Font(Font.HELVETICA, 11, Font.BOLD,   PRIMARY);
    private static final Font FONT_COL_HEADER = new Font(Font.HELVETICA,  8, Font.BOLD,   Color.WHITE);
    private static final Font FONT_CELL       = new Font(Font.HELVETICA,  8, Font.NORMAL, Color.BLACK);
    private static final Font FONT_MUSCLE     = new Font(Font.HELVETICA,  7, Font.ITALIC, Color.GRAY);
    private static final Font FONT_FOOTER     = new Font(Font.HELVETICA,  7, Font.NORMAL, Color.GRAY);

    @Override
    public byte[] generate(TrainingProgramExportData data) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            // ── Cabeçalho colorido ──────────────────────────────────
            PdfPTable header = new PdfPTable(1);
            header.setWidthPercentage(100);
            header.setSpacingAfter(16);

            PdfPCell hCell = new PdfPCell();
            hCell.setBackgroundColor(PRIMARY);
            hCell.setPadding(14);
            hCell.setBorder(Rectangle.NO_BORDER);
            hCell.addElement(new Paragraph(data.getProgramName(), FONT_PROG_TITLE));
            hCell.addElement(new Paragraph("Aluno: " + data.getUserName(), FONT_PROG_META));
            hCell.addElement(new Paragraph(
                "Emitido em " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                FONT_PROG_META
            ));
            header.addCell(hCell);
            doc.add(header);

            // ── Sem exercícios ──────────────────────────────────────
            if (data.getRows() == null || data.getRows().isEmpty()) {
                doc.add(new Paragraph(data.getEmptyMessage(), FONT_CELL));
                doc.close();
                return out.toByteArray();
            }

            // ── Fichas ─────────────────────────────────────────────
            for (Map.Entry<String, List<ExportRow>> entry : groupBySheet(data.getRows()).entrySet()) {

                // Título da ficha com barra lateral colorida
                PdfPTable sheetBar = new PdfPTable(new float[]{0.4f, 20f});
                sheetBar.setWidthPercentage(100);
                sheetBar.setSpacingBefore(10);
                sheetBar.setSpacingAfter(4);

                PdfPCell accent = new PdfPCell(new Phrase(" "));
                accent.setBackgroundColor(PRIMARY);
                accent.setBorder(Rectangle.NO_BORDER);
                sheetBar.addCell(accent);

                PdfPCell titleCell = new PdfPCell();
                titleCell.setBackgroundColor(PRIMARY_LIGHT);
                titleCell.setBorder(Rectangle.NO_BORDER);
                titleCell.setPadding(6);
                titleCell.addElement(new Paragraph(entry.getKey(), FONT_SHEET_NAME));
                sheetBar.addCell(titleCell);
                doc.add(sheetBar);

                // Tabela de exercícios
                PdfPTable table = new PdfPTable(new float[]{2.8f, 0.9f, 0.9f, 1.2f, 1.6f, 2.8f});
                table.setWidthPercentage(100);
                table.setSpacingAfter(8);

                for (String h : new String[]{"Exercício", "Séries", "Reps", "Descanso", "Músculo", "Observações"}) {
                    PdfPCell c = new PdfPCell(new Phrase(h, FONT_COL_HEADER));
                    c.setBackgroundColor(PRIMARY);
                    c.setPadding(5);
                    c.setBorderColor(BORDER);
                    table.addCell(c);
                }

                int rowIdx = 0;
                for (ExportRow row : entry.getValue()) {
                    Color bg = rowIdx % 2 == 0 ? Color.WHITE : ROW_ALT;
                    addCell(table, nvl(row.getExerciseName()), FONT_CELL, bg);
                    addCell(table, row.getSets() != null ? row.getSets() + "x" : "—", FONT_CELL, bg);
                    addCell(table, nvl(row.getReps()), FONT_CELL, bg);
                    addCell(table, row.getRestTimeInSeconds() != null ? row.getRestTimeInSeconds() + "s" : "—", FONT_CELL, bg);
                    addCell(table, nvl(row.getMuscleGroup()), FONT_MUSCLE, bg);
                    addCell(table, nvl(row.getTechniqueNotes()), FONT_CELL, bg);
                    rowIdx++;
                }

                doc.add(table);
            }

            // ── Rodapé ──────────────────────────────────────────────
            Paragraph footer = new Paragraph(
                "Ficha gerada automaticamente · " + data.getProgramName(),
                FONT_FOOTER
            );
            footer.setAlignment(Element.ALIGN_RIGHT);
            doc.add(footer);

            doc.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF medium", e);
        }
    }

    // ── helpers ────────────────────────────────────────────────

    private void addCell(PdfPTable table, String text, Font font, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(4);
        cell.setBorderColor(new Color(190, 190, 190));
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
