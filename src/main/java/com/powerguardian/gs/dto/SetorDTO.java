package com.powerguardian.gs.dto;

import com.powerguardian.gs.model.Setor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetorDTO {
    private Long id;
    
    @NotBlank(message = "Nome do setor é obrigatório")
    private String nome;
    
    private String descricao;
    
    @NotNull(message = "Capacidade de pacientes é obrigatória")
    @Min(value = 0, message = "Capacidade não pode ser negativa")
    private Integer capacidadePacientes;
    
    @NotNull(message = "Número de equipamentos críticos é obrigatório")
    @Min(value = 0, message = "Número de equipamentos não pode ser negativo")
    private Integer equipamentosCriticos;
    
    private Setor.StatusSetor status;
    
    @NotNull(message = "Informação sobre gerador é obrigatória")
    private Boolean temGerador;
    
    @NotNull(message = "Nível de prioridade é obrigatório")
    @Min(value = 1, message = "Prioridade mínima é 1")
    @Max(value = 5, message = "Prioridade máxima é 5")
    private Integer nivelPrioridade;
    
    private String criadoEm;
    private String atualizadoEm;
    private Integer quedaAtivas;
}