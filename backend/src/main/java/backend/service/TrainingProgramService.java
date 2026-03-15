package backend.service;

import backend.dto.request.training.TrainingProgramRequest;
import backend.dto.request.training.TrainingProgramUpdateRequest;
import backend.dto.response.training.TrainingProgramResponse;
import backend.infrastructure.exception.BadRequestException;
import backend.mapper.TrainingProgramMapper;
import backend.model.entity.TrainingProgram;
import backend.model.entity.User;
import backend.repository.TrainingProgramRepository;
import backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingProgramService {

    private final TrainingProgramRepository trainingProgramRepository;
    private final UserRepository userRepository;
    private final TrainingProgramMapper trainingProgramMapper;

    @Transactional
    public TrainingProgramResponse createTrainingProgram(TrainingProgramRequest trainingProgramRequest) {
        log.info("Criando programa de treinamento: {}", trainingProgramRequest.getName());

        if(trainingProgramRepository.existsByNameAndStudentId(trainingProgramRequest.getName(), trainingProgramRequest.getUserId())) {
            throw new BadRequestException("Já existe um programa de treinamento com este nome para este aluno");
        }

        User student = userRepository.findById(trainingProgramRequest.getUserId())
                .orElseThrow(() -> new BadRequestException("Aluno não encontrado ou inativo"));

        User trainer = null;
        if (trainingProgramRequest.getTrainerId() != null) {
             trainer = userRepository.findById(trainingProgramRequest.getTrainerId())
                    .orElseThrow(() -> new BadRequestException("Personal não encontrado ou inativo"));
        }

        TrainingProgram trainingProgram = TrainingProgram.builder().
                name(trainingProgramRequest.getName()).
                description(trainingProgramRequest.getDescription()).
//                programOrder(trainingProgramRequest.getProgramOrder()).
                student(student).
                trainer(trainer).
                active(true).
                build();

        TrainingProgram trainingProgramSaved = trainingProgramRepository.save(trainingProgram);
        log.info("Programa de treinamento criado com sucesso: {} (ID {})", trainingProgramSaved.getName(), trainingProgramSaved.getId());
//
        return trainingProgramMapper.toResponse(trainingProgramSaved);
    }

    @Transactional(readOnly = true)
    public TrainingProgramResponse getTrainingProgramById(Long id){
        log.info("Buscando programa de treinamento por ID: {}", id);

        TrainingProgram trainingProgram = trainingProgramRepository.findByIdWithTrainingSheet(id)
                .orElseThrow(() -> {
                    log.warn("Programa de treinamento não encontrado: ID {}", id);
                    return new BadRequestException("Programa de treinamento não encontrado");
                });

        return trainingProgramMapper.toResponse(trainingProgram);
    }

    @Transactional(readOnly = true)
    public List<TrainingProgramResponse> listTrainingPrograms(Long userId) {
        if(userId != null) {
            log.info("Listando todos os programas de treinamento do usuário");
            return trainingProgramRepository.findByStudentId(userId)
                    .stream()
                    .map(trainingProgramMapper::toResponse)
                    .toList();
        }
        log.info("Listando todos os programas de treinamento sem filtro de usuário");
        return trainingProgramRepository.findAll().stream().map(trainingProgramMapper::toResponse).toList();
    }

    @Transactional
    public TrainingProgramResponse updateTrainingPrograms(Long id, TrainingProgramUpdateRequest request){
        log.info("Atualizando programa de treinamento: ID {}", id);

        TrainingProgram trainingProgram = findTrainingProgramById(id);

        if (request.getName() != null
                && !request.getName().equals(trainingProgram.getName())
                && trainingProgramRepository.existsByNameAndStudentId(request.getName(), trainingProgram.getStudent().getId())) {
            throw new BadRequestException("Já existe um programa com este nome para este aluno");
        }

        trainingProgram.updateFrom(request);

        TrainingProgram updated = trainingProgramRepository.save(trainingProgram);
        log.info("Atualizando programa de treinamento: ID {}", id);

        return trainingProgramMapper.toResponse(updated);
    }

    @Transactional
    public TrainingProgramResponse assignTrainer(Long programId, Long trainerId) {
        log.info("Atribuindo personal {} ao programa {}", trainerId, programId);

        TrainingProgram program = findTrainingProgramById(programId);

        if (program.getTrainer() != null) {
            throw new BadRequestException("Programa já tem personal atribuído");
        }

        User trainer = userRepository.findById(trainerId)
                .orElseThrow(() -> new BadRequestException("Personal não encontrado"));

        program.setTrainer(trainer);

        TrainingProgram updated = trainingProgramRepository.save(program);

        return trainingProgramMapper.toResponse(updated);
    }

    @Transactional
    public TrainingProgramResponse removeTrainer(Long programId) {
        log.info("Removendo personal do programa {}", programId);

        TrainingProgram program = findTrainingProgramById(programId);

        if (program.getTrainer() == null) {
            throw new BadRequestException("Programa não tem personal");
        }

        program.setTrainer(null);

        TrainingProgram updated = trainingProgramRepository.save(program);

        return trainingProgramMapper.toResponse(updated);
    }

    @Transactional
    public void activateProgram(Long id) {
        log.info("Ativando folha ID: {}", id);

        TrainingProgram trainingProgram = findTrainingProgramById(id);

        if (trainingProgram.getActive()) {
            throw new BadRequestException("Programa já está ativo");
        }

        trainingProgram.setActive(true);

        trainingProgramRepository.save(trainingProgram);

        log.info("Folha ativada: {}", trainingProgram.getName());
    }

    /**
     * Desativar folha (soft delete)
     */
    @Transactional
    public void deactivateProgram(Long id) {
        log.info("Desativando folha ID: {}", id);

        TrainingProgram trainingProgram = findTrainingProgramById(id);

        if (!trainingProgram.getActive()) {
            throw new BadRequestException("Programa já está inativo");
        }

        trainingProgram.setActive(false);

        trainingProgramRepository.save(trainingProgram);

        log.info("Folha desativada: {}", trainingProgram.getName());
    }

    @Transactional
    public void deleteProgram(Long id) {
        log.warn("Deletando permanentemente programa de treinamento ID: {}", id);

        TrainingProgram trainingProgram = findTrainingProgramById(id);

        if (!trainingProgram.getTrainingSheets().isEmpty()) {
            throw new BadRequestException("Não é possível deletar programa com folhas associadas");
        }

        trainingProgramRepository.delete(trainingProgram);
    }

    private TrainingProgram findTrainingProgramById(Long id) {
        return trainingProgramRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Programa de treinamento não encontrado: ID {}", id);
                    return new BadRequestException("Programa de treinamento não encontrado");
                });
    }
}
