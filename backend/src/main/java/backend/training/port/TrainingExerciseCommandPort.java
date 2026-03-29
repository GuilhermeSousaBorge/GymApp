package backend.training.port;

import backend.training.model.entity.TrainingExercise;

/**
 * PORTA: Training Exercise Command (Escrita)
 *
 * Define contrato para operações de ESCRITA de exercícios de treinamento.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: TrainingExerciseRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de ESCRITA
 */
public interface TrainingExerciseCommandPort {
    
    /**
     * Salva novo exercício de treinamento
     * @param exercise Exercício a ser salvo
     * @return Exercício salvo (com ID gerado)
     */
    TrainingExercise save(TrainingExercise exercise);
    
    /**
     * Atualiza exercício existente
     * @param exercise Exercício com dados atualizados
     * @return Exercício atualizado
     */
    TrainingExercise update(TrainingExercise exercise);
    
    /**
     * Deleta exercício por ID
     * @param id ID do exercício a deletar
     */
    void deleteById(Long id);
}

