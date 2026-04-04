package backend.exercise.port;

import backend.exercise.model.entity.ExerciseCategory;

import java.util.List;
import java.util.Optional;

/**
 * PORTA: Exercise Category Query (Leitura)
 *
 * Define contrato para operações de LEITURA de categorias de exercícios.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: ExerciseCategoryRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de LEITURA
 */
public interface ExerciseCategoryQueryPort {
    
    /**
     * Busca categoria por ID
     * @param id ID da categoria
     * @return Optional contendo categoria ou vazio
     */
    Optional<ExerciseCategory> findById(Long id);
    
    /**
     * Busca categoria por nome
     * @param name Nome da categoria
     * @return Optional contendo categoria ou vazio
     */
    Optional<ExerciseCategory> findByName(String name);
    
    /**
     * Lista todas as categorias ativas
     * @return Lista de categorias
     */
    List<ExerciseCategory> findAllActive();
    
    /**
     * Lista todas as categorias
     * @return Lista de categorias
     */
    List<ExerciseCategory> findAll();
}

