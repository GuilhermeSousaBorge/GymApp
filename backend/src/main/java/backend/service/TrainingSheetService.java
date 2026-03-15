package backend.service;

import backend.dto.request.training.TrainingSheetRequest;
import backend.dto.request.training.TrainingSheetUpdateRequest;
import backend.dto.response.training.TrainingSheetResponse;
import backend.infrastructure.exception.BadRequestException;
import backend.mapper.TrainingSheetMapper;
import backend.model.entity.TrainingProgram;
import backend.model.entity.TrainingSheet;
import backend.model.enums.DayOfWeek;
import backend.repository.TrainingProgramRepository;
import backend.repository.TrainingSheetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingSheetService {

    private final TrainingSheetRepository trainingSheetRepository;
    private final TrainingProgramRepository trainingProgramRepository;
    private final TrainingSheetMapper trainingSheetMapper;


    /**
     *  createSheet(request) - validar programa ok
     *  getSheetById(id) ok
     *  listAllSheets() ok
     *  getSheetsFromProgram(programId)
     *  getActiveSheetsFromProgram(programId)
     *  getSheetsByDayOfWeek(day)
     *  updateSheet(id, request)
     *  addTrainingDay(sheetId, day)
     *  removeTrainingDay(sheetId, day)
     *  reorderSheet(sheetId, newOrder)
     *  activateSheet(id)
     *  deactivateSheet(id)
     *  deleteSheet(id)
     */

    @Transactional
    public TrainingSheetResponse createSheet(TrainingSheetRequest request) {
        log.info("Criando ficha de treino: {}", request.getName());
        if(trainingSheetRepository.existsByName(request.getName())) {
            throw new BadRequestException("Já existe uma ficha de treino com este nome");
        }

        TrainingProgram program = trainingProgramRepository.findById(request.getTrainingProgramId())
                .orElseThrow(() -> new BadRequestException("Programa de treino não encontrado"));

        if(!program.getActive()) {
            throw new BadRequestException("Não é possível criar ficha de treino para programa inativo");
        }

        if(request.getWeekDays() == null || request.getWeekDays().isEmpty()) {
            throw new BadRequestException("A ficha de treino deve ter pelo menos um dia da semana definido");
        }

        if(request.getWeekDays().size() > 7) {
            throw new BadRequestException("A ficha de treino não pode ter mais de 7 dias da semana");
        }

        TrainingSheet sheet = TrainingSheet.builder()
                .name(request.getName())
                .description(request.getDescription())
                .trainingProgram(program)
                .orderInProgram(trainingSheetRepository.countByTrainingProgramId(program.getId()) + 1)
                .weekdays(request.getWeekDays())
                .active(true)
                .build();

        TrainingSheet savedSheet = trainingSheetRepository.save(sheet);
        log.info("Ficha de treino criada com sucesso: {} (ID {})", savedSheet.getName(), savedSheet.getId());
        return trainingSheetMapper.toResponse(savedSheet);
    }

    @Transactional(readOnly = true)
    public TrainingSheetResponse getSheetById(Long id) {
        log.info("Buscando ficha de treino por ID: {}", id);

        TrainingSheet sheet = trainingSheetRepository.findById(id).orElseThrow(() -> new BadRequestException("Ficha de treino não encontrada"));

        return trainingSheetMapper.toResponse(sheet);
    }

    public List<TrainingSheetResponse> listAllSheets() {
        return trainingSheetRepository.findAll().stream().map(trainingSheetMapper::toResponse).toList();
    }

    /**
     * Buscar folhas de um programa
     */
    @Transactional(readOnly = true)
    public List<TrainingSheetResponse> getSheetsFromProgram(Long programId) {
        log.info("Buscando folhas do programa ID: {}", programId);

        // Validar que programa existe
        if (!trainingProgramRepository.existsById(programId)) {
            throw new BadRequestException("Programa não encontrado");
        }

        return trainingSheetRepository.findByTrainingProgramId(programId)
                .stream()
                .map(trainingSheetMapper::toResponse)
                .toList();
    }

    /**
     * Buscar folhas ativas de um programa
     */
    @Transactional(readOnly = true)
    public List<TrainingSheetResponse> getActiveSheetsFromProgram(Long programId) {
        log.info("Buscando folhas ativas do programa ID: {}", programId);

        if (!trainingProgramRepository.existsById(programId)) {
            throw new BadRequestException("Programa não encontrado");
        }

        return trainingSheetRepository.findByTrainingProgramIdAndActiveTrue(programId)
                .stream()
                .map(trainingSheetMapper::toResponse)
                .toList();
    }

    /**
     * Buscar folhas que treinam em determinado dia
     */
    @Transactional(readOnly = true)
    public List<TrainingSheetResponse> getSheetsByDayOfWeek(DayOfWeek dayOfWeek) {
        log.info("Buscando folhas que treinam em: {}", dayOfWeek);

        return trainingSheetRepository.findByWeekdaysContaining(dayOfWeek)
                .stream()
                .map(trainingSheetMapper::toResponse)
                .toList();
    }

    /**
     * Atualizar folha
     */
    @Transactional
    public TrainingSheetResponse updateSheet(Long id, TrainingSheetUpdateRequest request) {
        log.info("Atualizando folha ID: {}", id);

        TrainingSheet sheet = findSheetById(id);

        // Validar nome duplicado (se mudou)
        if (request.getName() != null
                && !request.getName().equals(sheet.getName())
                && trainingSheetRepository.existsByNameAndTrainingProgramId(
                request.getName(), sheet.getTrainingProgram().getId())) {
            throw new BadRequestException("Já existe uma folha com este nome neste programa");
        }

        // Validar weekdays (se mudou)
        if (request.getWeekdays() != null) {
            if (request.getWeekdays().isEmpty()) {
                throw new BadRequestException("Folha deve ter pelo menos 1 dia de treino");
            }
            if (request.getWeekdays().size() > 7) {
                throw new BadRequestException("Não pode ter mais de 7 dias na semana");
            }
        }

        // Atualizar campos
        sheet.updateFrom(request);

        TrainingSheet updated = trainingSheetRepository.save(sheet);

        log.info("Folha atualizada: {}", updated.getName());

        return trainingSheetMapper.toResponse(updated);
    }

    /**
     * Adicionar dia de treino
     */
    @Transactional
    public TrainingSheetResponse addTrainingDay(Long sheetId, DayOfWeek dayOfWeek) {
        log.info("Adicionando dia {} na folha {}", dayOfWeek, sheetId);

        TrainingSheet sheet = findSheetById(sheetId);

        // Validar se já treina nesse dia
        if (sheet.getWeekdays().contains(dayOfWeek)) {
            throw new BadRequestException("Folha já treina em " + dayOfWeek.getPortugueseName());
        }

        // Validar máximo 7 dias
        if (sheet.getWeekdays().size() >= 7) {
            throw new BadRequestException("Folha já treina todos os dias da semana");
        }

        // Adicionar dia
        sheet.getWeekdays().add(dayOfWeek);

        TrainingSheet updated = trainingSheetRepository.save(sheet);

        log.info("Dia {} adicionado na folha {}", dayOfWeek, sheet.getName());

        return trainingSheetMapper.toResponse(updated);
    }

    /**
     * Remover dia de treino
     */
    @Transactional
    public TrainingSheetResponse removeTrainingDay(Long sheetId, DayOfWeek dayOfWeek) {
        log.info("Removendo dia {} da folha {}", dayOfWeek, sheetId);

        TrainingSheet sheet = findSheetById(sheetId);

        // Validar se treina nesse dia
        if (!sheet.getWeekdays().contains(dayOfWeek)) {
            throw new BadRequestException("Folha não treina em " + dayOfWeek.getPortugueseName());
        }

        // Validar mínimo 1 dia
        if (sheet.getWeekdays().size() <= 1) {
            throw new BadRequestException("Folha deve ter pelo menos 1 dia de treino");
        }

        // Remover dia
        sheet.getWeekdays().remove(dayOfWeek);

        TrainingSheet updated = trainingSheetRepository.save(sheet);

        log.info("Dia {} removido da folha {}", dayOfWeek, sheet.getName());

        return trainingSheetMapper.toResponse(updated);
    }

    /**
     * Alterar ordem da folha no programa
     */
    @Transactional
    public TrainingSheetResponse reorderSheet(Long sheetId, Integer newOrder) {
        log.info("Alterando ordem da folha {} para {}", sheetId, newOrder);

        TrainingSheet sheet = findSheetById(sheetId);

        if (newOrder == null || newOrder < 1) {
            throw new BadRequestException("Ordem deve ser maior que 0");
        }

        sheet.setOrderInProgram(newOrder);

        TrainingSheet updated = trainingSheetRepository.save(sheet);

        log.info("Ordem da folha {} alterada para {}", sheet.getName(), newOrder);

        return trainingSheetMapper.toResponse(updated);
    }

    /**
     * Ativar folha
     */
    @Transactional
    public void activateSheet(Long id) {
        log.info("Ativando folha ID: {}", id);

        TrainingSheet sheet = findSheetById(id);

        if (sheet.getActive()) {
            throw new BadRequestException("Folha já está ativa");
        }

        sheet.setActive(true);

        trainingSheetRepository.save(sheet);

        log.info("Folha ativada: {}", sheet.getName());
    }

    /**
     * Desativar folha (soft delete)
     */
    @Transactional
    public void deactivateSheet(Long id) {
        log.info("Desativando folha ID: {}", id);

        TrainingSheet sheet = findSheetById(id);

        if (!sheet.getActive()) {
            throw new BadRequestException("Folha já está inativa");
        }

        sheet.setActive(false);

        trainingSheetRepository.save(sheet);

        log.info("Folha desativada: {}", sheet.getName());
    }

    /**
     * Deletar folha permanentemente
     */
    @Transactional
    public void deleteSheet(Long id) {
        log.warn("Deletando permanentemente folha ID: {}", id);

        TrainingSheet sheet = findSheetById(id);

        // TODO: Verificar se tem exercícios associados
        // if (!sheet.getExercises().isEmpty()) {
        //     throw new BadRequestException("Não é possível deletar folha com exercícios");
        // }

        trainingSheetRepository.delete(sheet);

        log.warn("Folha deletada: {}", sheet.getName());
    }

    private TrainingSheet findSheetById(Long id) {
        return trainingSheetRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ficha de treino não encontrada para ID: {}", id);
                    return new BadRequestException("Ficha de treino não encontrada para ID: " + id);
                });
    }

}
