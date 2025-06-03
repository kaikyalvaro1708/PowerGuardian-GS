package com.powerguardian.gs.repository;

import com.powerguardian.gs.model.Setor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetorRepository extends JpaRepository<Setor, Long> {
    
    List<Setor> findByStatus(Setor.StatusSetor status);
    
    List<Setor> findByTemGeradorTrue();
    
    List<Setor> findByNivelPrioridadeLessThanEqual(Integer prioridade);
    
    @Query("SELECT s FROM Setor s WHERE s.capacidadePacientes > :capacidade")
    List<Setor> findByCapacidadeMinima(Integer capacidade);
    
    @Query("SELECT COUNT(s) FROM Setor s WHERE s.status != 'NORMAL'")
    Long countSetoresAfetados();
    
    @Query("SELECT s FROM Setor s LEFT JOIN FETCH s.quedasEnergia qe WHERE qe.status = 'ATIVA'")
    List<Setor> findSetoresComQuedasAtivas();
    
    @Query("SELECT s FROM Setor s ORDER BY s.nivelPrioridade ASC")
    List<Setor> findAllOrderByPrioridade();
}
