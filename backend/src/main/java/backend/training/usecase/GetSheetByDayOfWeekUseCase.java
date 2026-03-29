package backend.training.usecase;

import backend.training.dto.TrainingSheetResponse;
import backend.training.mapper.TrainingSheetMapper;
import backend.training.model.enums.DayOfWeek;
import backend.training.port.TrainingSheetQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GetSheetByDayOfWeekUseCase {

    private final TrainingSheetQueryPort queryPort;
    private final TrainingSheetMapper mapper;

    @Transactional(readOnly = true)
    public List<TrainingSheetResponse> execute(DayOfWeek dayOfWeek){
        log.info("Buscando folhas que treinam em: {}", dayOfWeek);

        return queryPort.findByDayOfWeek(dayOfWeek)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
