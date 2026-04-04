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
 * Layout ELABORADO — dark mode, cards de resumo, badges de músculo,
 * métricas destacadas e paginação automática no rodapé.
 */
@Component("pdfExporterElaborado")
public class TrainingProgramPdfElaborado implements TrainingProgramPdfExporterPort {

    // Paleta dark
    private static final Color BG_DARK       = new Color(18,  18,  28);
    private static final Color ACCENT        = new Color(82, 196, 110);
    private static final Color ACCENT_DARK   = new Color(39, 130,  67);
    private static final Color SURFACE       = new Color(30,  30,  45);
    private static final Color SURFACE_LIGHT = new Color(40,  40,  58);
    private static final Color TEXT_PRIMARY  = new Color(240, 240, 240);
    private static final Color TEXT_MUTED    = new Color(150, 150, 170);
    private static final Color BORDER_DARK   = new Color(55,  55,  75);

    private static final Font FONT_PROG_TITLE = new Font(Font.HELVETICA, 22, Font.BOLD,   TEXT_PRIMARY);
    private static final Font FONT_PROG_META  = new Font(Font.HELVETICA,  9, Font.NORMAL, TEXT_MUTED);
    private static final Font FONT_SHEET_NAME = new Font(Font.HELVETICA, 12, Font.BOLD,   ACCENT);
    private static final Font FONT_COL_HEADER = new Font(Font.HELVETICA,  8, Font.BOLD,   TEXT_MUTED);
    private static final Font FONT_EX_NAME    = new Font(Font.HELVETICA,  9, Font.BOLD,   TEXT_PRIMARY);
    private static final Font FONT_EX_EQUIP   = new Font(Font.HELVETICA,  7, Font.ITALIC, TEXT_MUTED);
    private static final Font FONT_METRIC_VAL = new Font(Font.HELVETICA, 10, Font.BOLD,   ACCENT);
    private static final Font FONT_METRIC_LBL = new Font(Font.HELVETICA,  7, Font.NORMAL, TEXT_MUTED);
    private static final Font FONT_BADGE      = new Font(Font.HELVETICA,  7, Font.BOLD,   Color.WHITE);
    private static final Font FONT_NOTES      = new Font(Font.HELVETICA,  8, Font.ITALIC, TEXT_MUTED);
    private static final Font FONT_FOOTER     = new Font(Font.HELVETICA,  7, Font.NORMAL, TEXT_MUTED);
    private static final Font FONT_STAT_VAL   = new Font(Font.HELVETICA, 13, Font.BOLD,   ACCENT);
    private static final Font FONT_STAT_LBL   = new Font(Font.HELVETICA,  7, Font.NORMAL, TEXT_MUTED);

    @Override
    public byte[] generate(TrainingProgramExportData data) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document doc = new Document(PageSize.A4, 36, 36, 36, 50);
            PdfWriter writer = PdfWriter.getInstance(doc, out);

            // Rodapé com paginação
            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter w, Document d) {
                    PdfContentByte cb = w.getDirectContent();
                    cb.setColorStroke(BORDER_DARK);
//                    cb.moveTo(36, 38);
                    cb.lineTo(d.getPageSize().getWidth() - 36, 38);
                    cb.stroke();

                    ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                        new Phrase(data.getProgramName() + " · " + data.getUserName(), FONT_FOOTER),
                        36, 28, 0);
                    ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                        new Phrase("Página " + w.getPageNumber(), FONT_FOOTER),
                        d.getPageSize().getWidth() - 36, 28, 0);
                }
            });

            doc.open();

            // ── Hero ────────────────────────────────────────────────
            PdfPTable hero = new PdfPTable(1);
            hero.setWidthPercentage(100);
            hero.setSpacingAfter(20);

            PdfPCell heroCell = new PdfPCell();
            heroCell.setBackgroundColor(BG_DARK);
            heroCell.setPadding(16);
            heroCell.setBorder(Rectangle.NO_BORDER);

            // Linha accent no topo
            PdfPTable accentBar = new PdfPTable(1);
            accentBar.setWidthPercentage(100);
            PdfPCell bar = new PdfPCell(new Phrase(" "));
            bar.setBackgroundColor(ACCENT);
            bar.setFixedHeight(3);
            bar.setBorder(Rectangle.NO_BORDER);
            accentBar.addCell(bar);
            heroCell.addElement(accentBar);
            heroCell.addElement(new Paragraph(" "));

            heroCell.addElement(new Paragraph(data.getProgramName().toUpperCase(), FONT_PROG_TITLE));
            heroCell.addElement(new Paragraph(
                "Aluno: " + data.getUserName()
                + "   ·   Emitido em " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                FONT_PROG_META
            ));
            heroCell.addElement(new Paragraph(" "));

            // Cards de resumo
            if (data.getRows() != null && !data.getRows().isEmpty()) {
                long totalSheets = data.getRows().stream()
                    .map(ExportRow::getSheetName)
                    .filter(Objects::nonNull)
                    .distinct().count();

                PdfPTable stats = new PdfPTable(3);
                stats.setWidthPercentage(55);
                addStatCard(stats, String.valueOf(totalSheets), "Fichas");
                addStatCard(stats, String.valueOf(data.getRows().size()), "Exercícios");
                long muscles = data.getRows().stream()
                    .map(ExportRow::getMuscleGroup)
                    .filter(Objects::nonNull)
                    .distinct().count();
                addStatCard(stats, String.valueOf(muscles), "Grupos musculares");
                heroCell.addElement(stats);
            }

            hero.addCell(heroCell);
            doc.add(hero);

            // ── Sem exercícios ──────────────────────────────────────
            if (data.getRows() == null || data.getRows().isEmpty()) {
                doc.add(new Paragraph(data.getEmptyMessage(), FONT_PROG_META));
                doc.close();
                return out.toByteArray();
            }

            // ── Fichas ─────────────────────────────────────────────
            for (Map.Entry<String, List<ExportRow>> entry : groupBySheet(data.getRows()).entrySet()) {

                // Título da ficha
                PdfPTable sheetBar = new PdfPTable(1);
                sheetBar.setWidthPercentage(100);
                sheetBar.setSpacingBefore(14);
                sheetBar.setSpacingAfter(4);

                PdfPCell stc = new PdfPCell();
                stc.setBackgroundColor(SURFACE);
                stc.setBorder(Rectangle.LEFT);
                stc.setBorderColorLeft(ACCENT);
                stc.setBorderWidthLeft(3f);
                stc.setPadding(8);
                stc.addElement(new Paragraph(entry.getKey().toUpperCase(), FONT_SHEET_NAME));
                sheetBar.addCell(stc);
                doc.add(sheetBar);

                // Tabela
                PdfPTable table = new PdfPTable(new float[]{0.5f, 2.6f, 0.8f, 0.8f, 1.1f, 1.6f, 2.4f});
                table.setWidthPercentage(100);
                table.setSpacingAfter(6);

                for (String h : new String[]{"#", "Exercício", "Séries", "Reps", "Descanso", "Músculo", "Observações"}) {
                    PdfPCell c = new PdfPCell(new Phrase(h, FONT_COL_HEADER));
                    c.setBackgroundColor(SURFACE_LIGHT);
                    c.setPadding(5);
                    c.setBorderColor(BORDER_DARK);
                    table.addCell(c);
                }

                int idx = 1;
                for (ExportRow row : entry.getValue()) {
                    Color rowBg = idx % 2 == 0 ? SURFACE_LIGHT : SURFACE;

                    // #
                    PdfPCell numCell = new PdfPCell(new Phrase(String.format("%02d", idx), FONT_COL_HEADER));
                    numCell.setBackgroundColor(rowBg);
                    numCell.setPadding(5);
                    numCell.setBorderColor(BORDER_DARK);
                    numCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(numCell);

                    // Nome + equipamento
                    PdfPCell nameCell = new PdfPCell();
                    nameCell.setBackgroundColor(rowBg);
                    nameCell.setPadding(5);
                    nameCell.setBorderColor(BORDER_DARK);
                    nameCell.addElement(new Paragraph(nvl(row.getExerciseName()), FONT_EX_NAME));
                    if (row.getEquipment() != null && !row.getEquipment().isBlank()) {
                        nameCell.addElement(new Paragraph(row.getEquipment(), FONT_EX_EQUIP));
                    }
                    table.addCell(nameCell);

                    // Métricas
                    addMetricCell(table, row.getSets() != null ? String.valueOf(row.getSets()) : "—", "x", rowBg);
                    addMetricCell(table, nvl(row.getReps()), "reps", rowBg);
                    addMetricCell(table,
                        row.getRestTimeInSeconds() != null ? String.valueOf(row.getRestTimeInSeconds()) : "—",
                        "seg", rowBg);

                    // Badge músculo
                    PdfPCell muscleCell = new PdfPCell();
                    muscleCell.setBackgroundColor(rowBg);
                    muscleCell.setPadding(5);
                    muscleCell.setBorderColor(BORDER_DARK);
                    muscleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    if (row.getMuscleGroup() != null && !row.getMuscleGroup().isBlank()) {
                        PdfPTable badge = new PdfPTable(1);
                        badge.setWidthPercentage(95);
                        PdfPCell badgeCell = new PdfPCell(new Phrase(row.getMuscleGroup(), FONT_BADGE));
                        badgeCell.setBackgroundColor(ACCENT_DARK);
                        badgeCell.setPadding(3);
                        badgeCell.setBorder(Rectangle.NO_BORDER);
                        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        badge.addCell(badgeCell);
                        muscleCell.addElement(badge);
                    } else {
                        muscleCell.addElement(new Paragraph("—", FONT_NOTES));
                    }
                    table.addCell(muscleCell);

                    // Observações
                    PdfPCell notesCell = new PdfPCell(new Phrase(nvl(row.getTechniqueNotes()), FONT_NOTES));
                    notesCell.setBackgroundColor(rowBg);
                    notesCell.setPadding(5);
                    notesCell.setBorderColor(BORDER_DARK);
                    table.addCell(notesCell);

                    idx++;
                }

                doc.add(table);
            }

            doc.close();
            return out.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Erro ao gerar PDF elaborado", e);
        }
    }

    // ── helpers ────────────────────────────────────────────────

    private void addStatCard(PdfPTable table, String value, String label) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(SURFACE);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(8);
        cell.addElement(new Paragraph(value, FONT_STAT_VAL));
        cell.addElement(new Paragraph(label, FONT_STAT_LBL));
        table.addCell(cell);
    }

    private void addMetricCell(PdfPTable table, String value, String label, Color bg) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(bg);
        cell.setPadding(5);
        cell.setBorderColor(BORDER_DARK);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.addElement(new Paragraph(value, FONT_METRIC_VAL));
        cell.addElement(new Paragraph(label, FONT_METRIC_LBL));
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
