package backend.training.usecase;

import backend.exercise.model.entity.Exercise;
import backend.exercise.port.ExerciseQueryPort;
import backend.infrastructure.exception.BadRequestException;
import backend.training.dto.TrainingExerciseRequest;
import backend.training.dto.TrainingExerciseResponse;
import backend.training.mapper.TrainingExerciseMapper;
import backend.training.model.entity.TrainingExercise;
import backend.training.model.entity.TrainingSheet;
import backend.training.port.TrainingExerciseCommandPort;
import backend.training.port.TrainingExerciseQueryPort;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("trainingCreateExerciseUseCase")
@Slf4j
@RequiredArgsConstructor
public class CreateExerciseUseCase {

    private final TrainingExerciseQueryPort exerciseQueryPort;
    private final TrainingExerciseCommandPort exerciseCommandPort;
    private final TrainingSheetQueryPort sheetQueryPort;
    private final ExerciseQueryPort baseExerciseQueryPort;
    private final TrainingExerciseMapper mapper;

    @Transactional
    public TrainingExerciseResponse execute(TrainingExerciseRequest request){
        log.info("Criando exercício de treinamento: {}", request.getExerciseId());

        TrainingSheet trainingSheet = sheetQueryPort.findById(request.getTrainingSheetId())
                .orElseThrow(() -> new BadRequestException("Ficha de treinamento não encontrada"));
        if(!trainingSheet.getActive()) {
            throw new BadRequestException("Ficha de treinamento inativa");
        }

        Exercise exercise = baseExerciseQueryPort.findById(request.getExerciseId())
                .orElseThrow(() -> new BadRequestException("Exercício não encontrado"));

        if(!exercise.getActive()) {
            throw new BadRequestException("Exercício inativo");
        }

        if (exerciseQueryPort.existsBySheetAndExercise(request.getTrainingSheetId(), request.getExerciseId())) {
            throw new BadRequestException("Exercício já adicionado à ficha de treinamento");
        }

        TrainingExercise newTrainingExercise = TrainingExercise.builder()
                .trainingSheet(trainingSheet)
                .exercise(exercise)
                .sets(request.getSets())
                .reps(request.getReps())
                .orderInSheet(exerciseQueryPort.countBySheet(request.getTrainingSheetId()) + 1)
                .build();

        exerciseCommandPort.save(newTrainingExercise);

        return mapper.toResponse(newTrainingExercise);
    }

}
