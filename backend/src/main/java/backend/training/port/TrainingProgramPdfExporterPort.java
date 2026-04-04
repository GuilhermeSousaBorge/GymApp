package backend.training.port;

import backend.training.dto.TrainingProgramExportData;

public interface TrainingProgramPdfExporterPort {

    byte[] generate(TrainingProgramExportData data);
}

