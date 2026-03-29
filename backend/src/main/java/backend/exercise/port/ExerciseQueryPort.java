package backend.exercise.port;

import backend.exercise.model.entity.Exercise;

import java.util.List;
import java.util.Optional;

/**
 * PORTA: Exercise Query (Leitura)
 *
 * Define contrato para operações de LEITURA de exercícios.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: ExerciseRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de LEITURA
 */
public interface ExerciseQueryPort {
    
    /**
     * Busca exercício por ID
     * @param id ID do exercício
     * @return Optional contendo exercício ou vazio
     */
    Optional<Exercise> findById(Long id);
    
    /**
     * Busca exercício com categoria carregada (fetch eager)
     * @param id ID do exercício
     * @return Optional contendo exercício ou vazio
     */
    Optional<Exercise> findByIdWithCategory(Long id);
    
    /**
     * Lista todos os exercícios ativos
     * @return Lista de exercícios
     */
    List<Exercise> findAllActive();
    
    /**
     * Lista exercícios de uma categoria
     * @param categoryId ID da categoria
     * @return Lista de exercícios
     */
    List<Exercise> findByCategory(Long categoryId);
    
    /**
     * Lista exercícios ativos de uma categoria
     * @param categoryId ID da categoria
     * @return Lista de exercícios
     */
    List<Exercise> findByCategoryActive(Long categoryId);
    
    /**
     * Busca exercícios por termo de busca
     * @param searchTerm Termo a buscar (nome ou equipamento)
     * @return Lista de exercícios encontrados
     */
    List<Exercise> search(String searchTerm);
    
    /**
     * Lista todos os exercícios
     * @return Lista de exercícios
     */
    List<Exercise> findAll();
}


