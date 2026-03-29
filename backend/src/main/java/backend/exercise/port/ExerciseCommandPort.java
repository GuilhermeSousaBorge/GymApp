package backend.exercise.port;

import backend.exercise.model.entity.Exercise;

/**
 * PORTA: Exercise Command (Escrita)
 *
 * Define contrato para operações de ESCRITA de exercícios.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: ExerciseRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de ESCRITA
 */
public interface ExerciseCommandPort {
    
    /**
     * Salva novo exercício
     * @param exercise Exercício a ser salvo
     * @return Exercício salvo (com ID gerado)
     */
    Exercise save(Exercise exercise);
    
    /**
     * Atualiza exercício existente
     * @param exercise Exercício com dados atualizados
     * @return Exercício atualizado
     */
    Exercise update(Exercise exercise);
    
    /**
     * Deleta exercício por ID
     * @param id ID do exercício a deletar
     */
    void deleteById(Long id);
}

