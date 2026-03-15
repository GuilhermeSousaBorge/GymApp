package backend.mapper;

import backend.dto.response.training.TrainingSheetResponse;
import backend.model.entity.TrainingSheet;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainingSheetMapper {

    public TrainingSheetResponse toResponse(TrainingSheet sheet) {
        if (sheet == null) return null;

        return TrainingSheetResponse.builder()
                .id(sheet.getId())
                .name(sheet.getName())
                .description(sheet.getDescription())
                .orderInProgram(sheet.getOrderInProgram())
                .active(sheet.getActive())
                .weekdays(sheet.getWeekdays())
                .restTimeSeconds(sheet.getRestTimeSeconds())
                .trainingProgramId(sheet.getTrainingProgram().getId())
                .createdAt(sheet.getCreatedAt())
                .updatedAt(sheet.getUpdatedAt())
                .build();
    }
}