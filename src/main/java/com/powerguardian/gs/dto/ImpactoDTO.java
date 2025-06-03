package com.powerguardian.gs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactoDTO {
    private ResumoImpacto resumoGeral;
    private List<ImpactoSetorDTO> impactoPorSetor;
    private AnaliseFinanceira analiseFinanceira;
    private List<EquipamentoCritico> equipamentosCriticos;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResumoImpacto {
        private Integer totalPacientesAfetados;
        private Integer totalEquipamentosAfetados;
        private Integer totalQuedas;
        private Long tempoMedioQueda;
        private String nivelRiscoGeral;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ImpactoSetorDTO {
        private String nomeSetor;
        private Integer pacientesAfetados;
        private Integer equipamentosAfetados;
        private BigDecimal custoEstimado;
        private String status;
        private Integer nivelPrioridade;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnaliseFinanceira {
        private BigDecimal custoEquipamentos;
        private BigDecimal receitaPerdida;
        private BigDecimal custoEmergencia;
        private BigDecimal custoTotal;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EquipamentoCritico {
        private String nome;
        private String setor;
        private String status;
        private Integer pacientesAfetados;
        private String tempoInatividade;
    }
}