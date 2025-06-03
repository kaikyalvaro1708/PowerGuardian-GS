package com.powerguardian.gs.dto;

import com.powerguardian.gs.model.QuedaEnergia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuedaEnergiaDTO {
    private Long id;
    
    @NotNull(message = "ID do setor é obrigatório")
    private Long setorId;
    
    private String nomeSetor;
    
    private String inicioQueda;
    private String fimQueda;
    
    @NotNull(message = "Severidade é obrigatória")
    private QuedaEnergia.SeveridadeQueda severidade;
    
    @NotNull(message = "Número de pacientes afetados é obrigatório")
    @Min(value = 0, message = "Número de pacientes não pode ser negativo")
    private Integer pacientesAfetados;
    
    @NotNull(message = "Número de equipamentos afetados é obrigatório")
    @Min(value = 0, message = "Número de equipamentos não pode ser negativo")
    private Integer equipamentosAfetados;
    
    private BigDecimal custoEstimado;
    private String observacoes;
    private String causaProvavel;
    private QuedaEnergia.StatusQueda status;
    private String previsaoRestauracao;
    private String criadoEm;
    private Long duracaoMinutos;
}