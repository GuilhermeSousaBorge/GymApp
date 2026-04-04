package backend.training.usecase;

import backend.training.dto.TrainingProgramResponse;
import backend.training.mapper.TrainingProgramMapper;
import backend.training.port.TrainingProgramQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ListProgramUseCase {

    private final TrainingProgramQueryPort queryPort;
    private final TrainingProgramMapper mapper;

    @Transactional(readOnly = true)
    public List<TrainingProgramResponse> execute(Long userId){
        if(userId != null) {
            log.info("Listando todos os programas de treinamento do usuário");
            return queryPort.findByStudentId(userId)
                    .stream()
                    .map(mapper::toResponse)
                    .toList();
        }

        log.info("Listando todos os programas de treinamento sem filtro de usuário");
        return queryPort.findAll().stream().map(mapper::toResponse).toList();
    }
}
