package com.powerguardian.gs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "setores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Column(nullable = false)
    private Integer capacidadePacientes;
    
    @Column(nullable = false)
    private Integer equipamentosCriticos;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusSetor status;
    
    @Column(nullable = false)
    private Boolean temGerador;
    
    @Column(nullable = false)
    private Integer nivelPrioridade;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    private LocalDateTime atualizadoEm;
    
    @OneToMany(mappedBy = "setor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuedaEnergia> quedasEnergia;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
    
    public enum StatusSetor {
        NORMAL, ALERTA, CRITICO, SEM_ENERGIA
    }
}