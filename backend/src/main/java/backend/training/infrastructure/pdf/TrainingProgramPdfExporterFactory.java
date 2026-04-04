package backend.training.infrastructure.pdf;

import backend.training.port.TrainingProgramPdfExporterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TrainingProgramPdfExporterFactory {

    private final TrainingProgramPdfExporterPort simples;
    private final TrainingProgramPdfExporterPort medium;
    private final TrainingProgramPdfExporterPort elaborado;

    public TrainingProgramPdfExporterFactory(
            @Qualifier("pdfExporterSimples")   TrainingProgramPdfExporterPort simples,
            @Qualifier("pdfExporterMedium")    TrainingProgramPdfExporterPort medium,
            @Qualifier("pdfExporterElaborado") TrainingProgramPdfExporterPort elaborado
    ) {
        this.simples   = simples;
        this.medium    = medium;
        this.elaborado = elaborado;
    }

    public TrainingProgramPdfExporterPort resolve(String layout) {
        return switch (layout) {
            case "simple"     -> simples;
            case "elaborated" -> elaborado;
            default           -> medium;
        };
    }
}
