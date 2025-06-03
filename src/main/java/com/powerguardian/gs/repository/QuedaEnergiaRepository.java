package com.powerguardian.gs.repository;

import com.powerguardian.gs.model.QuedaEnergia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuedaEnergiaRepository extends JpaRepository<QuedaEnergia, Long> {
    
    List<QuedaEnergia> findByStatus(QuedaEnergia.StatusQueda status);
    
    List<QuedaEnergia> findBySetorId(Long setorId);
    
    List<QuedaEnergia> findBySeveridade(QuedaEnergia.SeveridadeQueda severidade);
    
    @Query("SELECT qe FROM QuedaEnergia qe WHERE qe.inicioQueda >= :dataInicio AND qe.inicioQueda <= :dataFim")
    List<QuedaEnergia> findByPeriodo(@Param("dataInicio") LocalDateTime dataInicio, 
                                   @Param("dataFim") LocalDateTime dataFim);
    
    @Query("SELECT COUNT(qe) FROM QuedaEnergia qe WHERE qe.status = 'ATIVA'")
    Long countQuedasAtivas();
    
    @Query("SELECT SUM(qe.pacientesAfetados) FROM QuedaEnergia qe WHERE qe.status = 'ATIVA'")
    Long sumPacientesAfetadosEmQuedasAtivas();
    
    @Query("SELECT SUM(qe.custoEstimado) FROM QuedaEnergia qe WHERE qe.status = 'ATIVA'")
    Double sumCustoEstimadoQuedasAtivas();
    
    @Query("SELECT qe FROM QuedaEnergia qe ORDER BY qe.criadoEm DESC")
    List<QuedaEnergia> findAllOrderByCreatedDesc();
    
    @Query("SELECT qe FROM QuedaEnergia qe WHERE qe.status = 'ATIVA' ORDER BY qe.severidade DESC, qe.inicioQueda ASC")
    List<QuedaEnergia> findQuedasAtivasOrderBySeveridade();
    
   @Query("SELECT COALESCE(AVG(CAST((EXTRACT(EPOCH FROM qe.fimQueda) - EXTRACT(EPOCH FROM qe.inicioQueda))/60 AS double)), 0.0) " +
           "FROM QuedaEnergia qe WHERE qe.fimQueda IS NOT NULL AND qe.inicioQueda IS NOT NULL")
    Double findAverageDurationInMinutes();
}