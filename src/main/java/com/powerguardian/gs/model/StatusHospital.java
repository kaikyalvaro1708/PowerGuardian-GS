package com.powerguardian.gs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusHospital {
    private StatusGeral statusGeral;
    private Integer setoresAfetados;
    private Integer totalSetores;
    private Integer pacientesAfetados;
    private Integer quedaAtivas;
    private String ultimaAtualizacao;
    
    public enum StatusGeral {
        NORMAL, ALERTA, CRITICO
    }
}