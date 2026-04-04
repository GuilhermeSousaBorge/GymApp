package backend.training.port;

import backend.training.model.entity.TrainingProgram;

/**
 * PORTA: Training Program Command (Escrita)
 *
 * Define contrato para operações de ESCRITA de programas de treinamento.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: TrainingProgramRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de ESCRITA
 */
public interface TrainingProgramCommandPort {
    
    /**
     * Salva novo programa de treinamento
     * @param program Programa a ser salvo
     * @return Programa salvo (com ID gerado)
     */
    TrainingProgram save(TrainingProgram program);
    
    /**
     * Atualiza programa existente
     * @param program Programa com dados atualizados
     * @return Programa atualizado
     */
    TrainingProgram update(TrainingProgram program);
    
    /**
     * Deleta programa por ID
     * @param id ID do programa a deletar
     */
    void deleteById(Long id);
}

