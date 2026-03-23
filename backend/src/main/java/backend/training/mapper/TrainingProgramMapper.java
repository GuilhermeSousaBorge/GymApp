package backend.training.mapper;

import backend.training.dto.TrainingProgramResponse;
import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.user.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TrainingProgramMapper {

    public TrainingProgramResponse toResponse(TrainingProgram program) {
        if (program == null) return null;

        return TrainingProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .active(program.getActive())
//                .programOrder(program.getProgramOrder())
                .student(toUserInfo(program.getStudent()))
                .trainer(toUserInfo(program.getTrainer()))  // Pode ser null
                .userId(program.getStudent().getId())
                .trainerId(program.getTrainer() != null ? program.getTrainer().getId() : null)
                .trainingSheets(toTrainingSheets(program))
                .createdAt(program.getCreatedAt())
                .updatedAt(program.getUpdatedAt())
                .build();
    }

    /**
     * Converte User para UserInfo
     */
    private TrainingProgramResponse.Userinfo toUserInfo(User user) {
        if (user == null) return null;

        return TrainingProgramResponse.Userinfo.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    /**
     * Converte lista de TrainingSheet para resumos
     */
    private List<TrainingProgramResponse.TrainingSheetInfo> toTrainingSheets(TrainingProgram program) {
        if (program.getTrainingSheets() == null || program.getTrainingSheets().isEmpty()) {
            return List.of();
        }

        return program.getTrainingSheets().stream()
                .map(this::toTrainingSheetInfo)
                .collect(Collectors.toList());
    }

    /**
     * Converte TrainingSheet para resumo
     */
    private TrainingProgramResponse.TrainingSheetInfo toTrainingSheetInfo(TrainingSheet sheet) {
        return TrainingProgramResponse.TrainingSheetInfo.builder()
                .id(sheet.getId())
                .name(sheet.getName())
                .orderInProgram(sheet.getOrderInProgram())
                .active(sheet.getActive())
                .build();
    }
}
