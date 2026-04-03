package backend.training.adapter;

import backend.training.dto.TrainingProgramExportData;
import backend.training.port.TrainingProgramPdfExporterPort;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class PdfTrainingProgramExporterAdapter implements TrainingProgramPdfExporterPort {

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 14, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 9, Font.BOLD);
    private static final Font CELL_FONT = new Font(Font.HELVETICA, 8);

    @Override
    public byte[] generate(TrainingProgramExportData data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph("Programa: " + safe(data.getProgramName()), TITLE_FONT));
            document.add(new Paragraph("Aluno: " + safe(data.getUserName())));
            document.add(new Paragraph(" "));

            List<TrainingProgramExportData.ExportRow> rows = data.getRows();
            if (rows == null || rows.isEmpty()) {
                document.add(new Paragraph(safe(data.getEmptyMessage())));
                document.close();
                return outputStream.toByteArray();
            }

            PdfPTable table = new PdfPTable(10);
            table.setWidthPercentage(100f);
            table.setHeaderRows(1);
            table.setWidths(new float[]{2.2f, 1f, 1f, 2.4f, 2.2f, 1f, 1f, 1.2f, 2.4f, 2.2f});

            addHeader(table, "Folha");
            addHeader(table, "Ord. Folha");
            addHeader(table, "Ord. Ex.");
            addHeader(table, "Exercicio");
            addHeader(table, "Grupo");
            addHeader(table, "Series");
            addHeader(table, "Reps");
            addHeader(table, "Descanso(s)");
            addHeader(table, "Tecnica");
            addHeader(table, "Equipamento");

            for (TrainingProgramExportData.ExportRow row : rows) {
                addCell(table, row.getSheetName());
                addCell(table, stringify(row.getSheetOrder()));
                addCell(table, stringify(row.getExerciseOrder()));
                addCell(table, row.getExerciseName());
                addCell(table, row.getMuscleGroup());
                addCell(table, stringify(row.getSets()));
                addCell(table, row.getReps());
                addCell(table, stringify(row.getRestTimeInSeconds()));
                addCell(table, row.getTechniqueNotes());
                addCell(table, row.getEquipment());
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException ex) {
            throw new IllegalStateException("Falha ao gerar PDF de treino", ex);
        } catch (Exception ex) {
            throw new IllegalStateException("Falha inesperada ao gerar arquivo de treino", ex);
        }
    }

    private void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(safe(text), CELL_FONT));
        cell.setPadding(4f);
        table.addCell(cell);
    }

    private String stringify(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}

