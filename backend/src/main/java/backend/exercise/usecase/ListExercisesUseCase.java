package backend.exercise.usecase;

import backend.exercise.dto.ExerciseResponse;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.port.ExerciseQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListExercisesUseCase {

    private final ExerciseQueryPort queryPort;
    private final ExerciseMapper mapper;

    @Transactional(readOnly = true)
    public List<ExerciseResponse> execute(){
        log.info("Listando todos os exercícios");

        return queryPort.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
