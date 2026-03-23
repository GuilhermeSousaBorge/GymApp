package backend.exercise.service;

import backend.exercise.dto.ExerciseRequest;
import backend.exercise.dto.ExerciseUpdateRequest;
import backend.exercise.dto.ExerciseResponse;
import backend.infrastructure.exception.BadRequestException;
import backend.exercise.mapper.ExerciseMapper;
import backend.exercise.model.entity.Exercise;
import backend.exercise.model.entity.ExerciseCategory;
import backend.exercise.repository.ExerciseCategoryRepository;
import backend.exercise.repository.ExerciseRepository;
import backend.training.repository.TrainingExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseCategoryRepository categoryRepository;
    private final ExerciseMapper exerciseMapper;
    private final TrainingExerciseRepository trainingExerciseRepository;

    /**
     * Criar novo exercício
     */
    @Transactional
    public ExerciseResponse createExercise(ExerciseRequest request) {
        log.info("Criando exercício: {}", request.getName());

        // Validar duplicação
        if (exerciseRepository.existsByName(request.getName())) {
            throw new BadRequestException("Exercício com este nome já existe");
        }

        // Buscar categoria
        ExerciseCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Categoria não encontrada"));

        // Verificar se categoria está ativa
        if (!category.getActive()) {
            throw new BadRequestException("Não é possível criar exercício em categoria inativa");
        }

        Exercise exercise = Exercise.builder()
                .name(request.getName())
                .description(request.getDescription())
                .equipment(request.getEquipment())
                .videoUrl(request.getVideoUrl())
                .category(category)
                .active(true)
                .build();

        Exercise savedExercise = exerciseRepository.save(exercise);

        log.info("Exercício criado com sucesso: {} (ID {})",
                savedExercise.getName(), savedExercise.getId());

        return exerciseMapper.toResponse(savedExercise);
    }

    /**
     * Buscar exercício por ID
     */
    @Transactional(readOnly = true)
    public ExerciseResponse getExerciseById(Long id) {
        log.info("Buscando exercício por ID: {}", id);

        // Usa query com JOIN FETCH para carregar categoria
        Exercise exercise = exerciseRepository.findByIdWithCategory(id)
                .orElseThrow(() -> {
                    log.warn("Exercício não encontrado para ID: {}", id);
                    return new BadRequestException("Exercício não encontrado");
                });

        return exerciseMapper.toResponse(exercise);
    }

    /**
     * Listar todos os exercícios
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> listAllExercises() {
        log.info("Listando todos os exercícios");

        return exerciseRepository.findAll()
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }

    /**
     * Listar apenas exercícios ativos
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> listActiveExercises() {
        log.info("Listando exercícios ativos");

        return exerciseRepository.findByActiveTrue()
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }

    /**
     * Listar exercícios por categoria
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> getExercisesByCategory(Long categoryId) {
        log.info("Buscando exercícios da categoria ID: {}", categoryId);

        // Validar se categoria existe
        if (!categoryRepository.existsById(categoryId)) {
            throw new BadRequestException("Categoria não encontrada");
        }

        return exerciseRepository.findByCategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }

    /**
     * Buscar exercícios (por nome ou equipamento)
     */
    @Transactional(readOnly = true)
    public List<ExerciseResponse> searchExercises(String searchTerm) {
        log.info("Buscando exercícios com termo: {}", searchTerm);

        return exerciseRepository.searchExercises(searchTerm)
                .stream()
                .map(exerciseMapper::toResponse)
                .toList();
    }

    /**
     * Atualizar exercício
     */
    @Transactional
    public ExerciseResponse updateExercise(Long id, ExerciseUpdateRequest request) {
        log.info("Atualizando exercício com ID: {}", id);

        Exercise exercise = findExerciseById(id);

        // Validar nome duplicado (se mudou)
        if (request.getName() != null
                && !request.getName().equals(exercise.getName())
                && exerciseRepository.existsByName(request.getName())) {
            throw new BadRequestException("Exercício com este nome já existe");
        }

        // Atualizar campos básicos
        exercise.updateFrom(request);

        // Atualizar categoria (se veio)
        if (request.getCategoryId() != null) {
            ExerciseCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BadRequestException("Categoria não encontrada"));

            if (!category.getActive()) {
                throw new BadRequestException("Não é possível associar a categoria inativa");
            }

            exercise.setCategory(category);
        }

        Exercise updatedExercise = exerciseRepository.save(exercise);

        log.info("Exercício atualizado com sucesso: {}", updatedExercise.getName());

        return exerciseMapper.toResponse(updatedExercise);
    }

    /**
     * Desativar exercício (soft delete)
     */
    @Transactional
    public void deactivateExercise(Long id) {
        log.info("Desativando exercício com ID: {}", id);

        Exercise exercise = findExerciseById(id);
        exercise.setActive(false);

        exerciseRepository.save(exercise);

        log.info("Exercício desativado: {}", exercise.getName());
    }

    /**
     * Ativar exercício
     */
    @Transactional
    public void activateExercise(Long id) {
        log.info("Ativando exercício com ID: {}", id);

        Exercise exercise = findExerciseById(id);
        exercise.setActive(true);

        exerciseRepository.save(exercise);

        log.info("Exercício ativado: {}", exercise.getName());
    }

    /**
     * Deletar permanentemente (apenas admin)
     */
    @Transactional
    public void deleteExercise(Long id) {
        log.warn("Deletando permanentemente exercício com ID: {}", id);

        Exercise exercise = findExerciseById(id);
        boolean isInUse = trainingExerciseRepository.existsByExerciseId(exercise.getId());

         if (isInUse) {
             throw new BadRequestException("Não é possível deletar exercício em uso");
         }

        exerciseRepository.delete(exercise);

        log.warn("Exercício deletado: {}", exercise.getName());
    }

    /**
     * Método auxiliar - Busca exercício ou lança exceção
     */
    private Exercise findExerciseById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Exercício não encontrado para ID: {}", id);
                    return new BadRequestException("Exercício não encontrado");
                });
    }
}
