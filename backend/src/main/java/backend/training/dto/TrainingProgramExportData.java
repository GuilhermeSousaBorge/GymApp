package backend.training.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TrainingProgramExportData {

    private Long programId;
    private String programName;
    private String userName;
    private List<ExportRow> rows;
    private String emptyMessage;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ExportRow {
        private String sheetName;
        private Integer sheetOrder;
        private Integer exerciseOrder;
        private String exerciseName;
        private String muscleGroup;
        private Integer sets;
        private String reps;
        private Integer restTimeInSeconds;
        private String techniqueNotes;
        private String equipment;
    }
}

