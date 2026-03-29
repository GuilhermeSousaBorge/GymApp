package backend.training.port;

import backend.training.model.entity.TrainingExercise;

import java.util.List;
import java.util.Optional;

/**
 * PORTA: Training Exercise Query (Leitura)
 *
 * Define contrato para operações de LEITURA de exercícios de treinamento.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: TrainingExerciseRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de LEITURA
 */
public interface TrainingExerciseQueryPort {
    
    /**
     * Busca exercício de treinamento por ID
     * @param id ID do exercício
     * @return Optional contendo exercício ou vazio
     */
    Optional<TrainingExercise> findById(Long id);
    
    /**
     * Busca exercícios de uma folha
     * @param sheetId ID da folha
     * @return Lista de exercícios
     */
    List<TrainingExercise> findBySheetId(Long sheetId);
    
    /**
     * Busca exercício com detalhes da base de exercícios (fetch eager)
     * @param id ID do exercício
     * @return Optional contendo exercício ou vazio
     */
    Optional<TrainingExercise> findByIdWithExerciseDetails(Long id);
    
    /**
     * Lista todos os exercícios de treinamento
     * @return Lista de exercícios
     */
    List<TrainingExercise> findAll();

    java.util.List<TrainingExercise> findBySheetWithExercise(Long sheetId);

    boolean existsBySheetAndExercise(Long sheetId, Long exerciseId);

    int countBySheet(Long sheetId);

    boolean existsBySheet(Long sheetId);
}

