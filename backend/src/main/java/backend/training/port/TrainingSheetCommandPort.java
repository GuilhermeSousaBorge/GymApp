package backend.training.port;

import backend.training.model.entity.TrainingSheet;

/**
 * PORTA: Training Sheet Command (Escrita)
 *
 * Define contrato para operações de ESCRITA de folhas de treinamento.
 * Inversão de dependência: Services dependem desta interface.
 *
 * Implementação: TrainingSheetRepositoryAdapter
 *
 * Princípio: Interface Segregation - apenas métodos de ESCRITA
 */
public interface TrainingSheetCommandPort {
    
    /**
     * Salva nova folha de treinamento
     * @param sheet Folha a ser salva
     * @return Folha salva (com ID gerado)
     */
    TrainingSheet save(TrainingSheet sheet);
    
    /**
     * Atualiza folha existente
     * @param sheet Folha com dados atualizados
     * @return Folha atualizada
     */
    TrainingSheet update(TrainingSheet sheet);
    
    /**
     * Deleta folha por ID
     * @param id ID da folha a deletar
     */
    void deleteById(Long id);
}

