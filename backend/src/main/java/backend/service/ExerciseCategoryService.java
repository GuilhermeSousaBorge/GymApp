package backend.service;

import backend.dto.request.exercise.ExerciseCategoryRequest;
import backend.dto.request.exercise.ExerciseCategoryUpdateRequest;
import backend.dto.response.exercise.ExerciseCategoryResponse;
import backend.infrastructure.exception.BadRequestException;
import backend.mapper.ExerciseCategoryMapper;
import backend.model.entity.ExerciseCategory;
import backend.repository.ExerciseCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExerciseCategoryService {

    private final ExerciseCategoryRepository categoryRepository;
    private final ExerciseCategoryMapper categoryMapper;

    /**
     * Criar nova categoria
     */
    @Transactional
    public ExerciseCategoryResponse createCategory(ExerciseCategoryRequest request) {
        log.info("Criando categoria: {}", request.getMuscleGroup());

        // Validar duplicação
        if (categoryRepository.existsByMuscleGroup(request.getMuscleGroup())) {
            throw new BadRequestException("Categoria com este nome já existe");
        }

        ExerciseCategory category = ExerciseCategory.builder()
                .muscleGroup(request.getMuscleGroup())
                .description(request.getDescription())
                .active(true)
                .build();

        ExerciseCategory savedCategory = categoryRepository.save(category);

        log.info("Categoria criada com sucesso: {} (ID {})",
                savedCategory.getMuscleGroup(), savedCategory.getId());

        return categoryMapper.toResponse(savedCategory);
    }

    /**
     * Buscar categoria por ID
     */
    @Transactional(readOnly = true)
    public ExerciseCategoryResponse getCategoryById(Long id) {
        log.info("Buscando categoria por ID: {}", id);

        ExerciseCategory category = findCategoryById(id);

        return categoryMapper.toResponse(category);
    }

    /**
     * Listar todas as categorias
     */
    @Transactional(readOnly = true)
    public List<ExerciseCategoryResponse> listAllCategories() {
        log.info("Listando todas as categorias");

        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    /**
     * Listar apenas categorias ativas
     */
    @Transactional(readOnly = true)
    public List<ExerciseCategoryResponse> listActiveCategories() {
        log.info("Listando categorias ativas");

        return categoryRepository.findByActiveTrue()
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    /**
     * Buscar categorias por grupo muscular
     */
    @Transactional(readOnly = true)
    public List<ExerciseCategoryResponse> getCategoriesByMuscleGroup(String muscleGroup) {
        log.info("Buscando categorias por grupo muscular: {}", muscleGroup);

        return categoryRepository.findByMuscleGroup(muscleGroup)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    /**
     * Atualizar categoria
     */
    @Transactional
    public ExerciseCategoryResponse updateCategory(Long id, ExerciseCategoryUpdateRequest request) {
        log.info("Atualizando categoria com ID: {}", id);

        ExerciseCategory category = findCategoryById(id);

        // Validar nome duplicado (se mudou)
        if (request.getMuscleGroup() != null
                && !request.getMuscleGroup().equals(category.getMuscleGroup())
                && categoryRepository.existsByMuscleGroup(request.getMuscleGroup())) {
            throw new BadRequestException("Categoria com este nome já existe");
        }

        // Atualizar campos
        category.updateFrom(request);

        ExerciseCategory updatedCategory = categoryRepository.save(category);

        log.info("Categoria atualizada com sucesso: {}", updatedCategory.getMuscleGroup());

        return categoryMapper.toResponse(updatedCategory);
    }

    /**
     * Desativar categoria (soft delete)
     */
    @Transactional
    public void deactivateCategory(Long id) {
        log.info("Desativando categoria com ID: {}", id);

        ExerciseCategory category = findCategoryById(id);
        category.setActive(false);

        categoryRepository.save(category);

        log.info("Categoria desativada: {}", category.getMuscleGroup());
    }

    /**
     * Ativar categoria
     */
    @Transactional
    public void activateCategory(Long id) {
        log.info("Ativando categoria com ID: {}", id);

        ExerciseCategory category = findCategoryById(id);
        category.setActive(true);

        categoryRepository.save(category);

        log.info("Categoria ativada: {}", category.getMuscleGroup());
    }

    /**
     * Deletar permanentemente (apenas admin)
     */
    @Transactional
    public void deleteCategory(Long id) {
        log.warn("Deletando permanentemente categoria com ID: {}", id);

        ExerciseCategory category = findCategoryById(id);

        // TODO: Verificar se tem exercícios associados antes de deletar
         if (!category.getExercises().isEmpty()) {
             throw new BadRequestException("Não é possível deletar categoria com exercícios associados");
         }

        categoryRepository.delete(category);

        log.warn("Categoria deletada: {}", category.getMuscleGroup());
    }

    /**
     * Método auxiliar - Busca categoria ou lança exceção
     */
    private ExerciseCategory findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Categoria não encontrada para ID: {}", id);
                    return new BadRequestException("Categoria não encontrada");
                });
    }
}
