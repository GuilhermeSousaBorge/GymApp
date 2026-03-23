package backend.infrastructure.security;

import backend.training.model.entity.TrainingProgram;
import backend.training.model.entity.TrainingSheet;
import backend.user.model.entity.User;
import backend.training.repository.TrainingExerciseRepository;
import backend.training.repository.TrainingProgramRepository;
import backend.training.repository.TrainingSheetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SecurityService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingSheetRepository trainingSheetRepository;
    private final TrainingExerciseRepository trainingExerciseRepository;

    public Boolean isProgramOwner(Long programId, Long userId) {
        TrainingProgram program = trainingProgramRepository
                .findById(programId)
                .orElse(null);

        if (program == null) {
            return false;
        }

        return program.getStudent().getId().equals(userId);
    }

    public Boolean isSheetOwner(Long sheetId, Long userId) {
        TrainingSheet sheet = trainingSheetRepository
                .findById(sheetId)
                .orElse(null);
        if (sheet == null) {
            return false;
        }
        return sheet.getTrainingProgram().getStudent().getId().equals(userId);
    }

    public Boolean isExerciseOwner(Long exerciseId, Long userId) {
        return trainingExerciseRepository.findById(exerciseId)
                .map(exercise -> exercise.getTrainingSheet().getTrainingProgram().getStudent().getId().equals(userId))
                .orElse(false);
    }

    public Boolean isSelf(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Long authenticatedUserId = ((User) Objects.requireNonNull(authentication.getPrincipal())).getId();

        return  id.equals(authenticatedUserId);
    }
}
