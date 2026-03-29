package backend.training.port;

import backend.training.model.entity.TrainingSheet;

import java.util.List;
import java.util.Optional;

/**
 * PORTA: Training Sheet Query (Leitura)
 *
 * Define contrato para operações de LEITURA de folhas de treinamento.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: TrainingSheetRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de LEITURA
 */
public interface TrainingSheetQueryPort {
    
    /**
     * Busca folha de treinamento por ID
     * @param id ID da folha
     * @return Optional contendo folha ou vazio
     */
    Optional<TrainingSheet> findById(Long id);
    
    /**
     * Busca folhas de um programa
     * @param programId ID do programa
     * @return Lista de folhas
     */
    List<TrainingSheet> findByProgramId(Long programId);
    
    /**
     * Busca folha com exercícios carregados (fetch eager)
     * @param id ID da folha
     * @return Optional contendo folha ou vazio
     */
    Optional<TrainingSheet> findByIdWithExercises(Long id);
    
    /**
     * Lista todas as folhas
     * @return Lista de folhas
     */
    List<TrainingSheet> findAll();

    boolean existsByName(String name);

    boolean existsByNameAndProgramId(String name, Long programId);

    int countByProgramId(Long programId);

    boolean existsById(Long id);

    List<TrainingSheet> findByProgramIdAndActive(Long programId);

    java.util.List<TrainingSheet> findByDayOfWeek(backend.training.model.enums.DayOfWeek dayOfWeek);
}

