package com.powerguardian.gs.dto;

import com.powerguardian.gs.model.StatusHospital;
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
public class DashboardResponseDTO {
    private StatusHospital.StatusGeral statusGeral;
    private Integer setoresAfetados;
    private Integer totalSetores;
    private Integer pacientesAfetados;
    private Integer quedaAtivas;
    private BigDecimal custoTotalEstimado;
    private String ultimaAtualizacao;
    private List<EventoRecenteDTO> eventosRecentes;
    private DadosEnergiaNacional dadosEnergiaNacional;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventoRecenteDTO {
        private Long id;
        private String setor;
        private String tipo;
        private String descricao;
        private String timestamp;
        private String severidade;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DadosEnergiaNacional {
        private String regiao;
        private String statusRede;
        private Double cargaAtual;
        private String fonte;
    }
}