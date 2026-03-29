package backend.exercise.port;

import backend.exercise.model.entity.ExerciseCategory;

/**
 * PORTA: Exercise Category Command (Escrita)
 *
 * Define contrato para operações de ESCRITA de categorias de exercícios.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: ExerciseCategoryRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de ESCRITA
 */
public interface ExerciseCategoryCommandPort {
    
    /**
     * Salva nova categoria
     * @param category Categoria a ser salva
     * @return Categoria salva (com ID gerado)
     */
    ExerciseCategory save(ExerciseCategory category);
    
    /**
     * Atualiza categoria existente
     * @param category Categoria com dados atualizados
     * @return Categoria atualizada
     */
    ExerciseCategory update(ExerciseCategory category);
    
    /**
     * Deleta categoria por ID
     * @param id ID da categoria a deletar
     */
    void deleteById(Long id);
}

