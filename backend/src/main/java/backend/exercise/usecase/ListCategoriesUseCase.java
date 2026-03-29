package backend.exercise.usecase;

import backend.exercise.dto.ExerciseCategoryResponse;
import backend.exercise.mapper.ExerciseCategoryMapper;
import backend.exercise.port.ExerciseCategoryQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListCategoriesUseCase {

    private final ExerciseCategoryQueryPort queryPort;
    private final ExerciseCategoryMapper mapper;

    @Transactional(readOnly = true)
    public List<ExerciseCategoryResponse> execute(){
        log.info("Listando todas as categorias");

        return queryPort.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
