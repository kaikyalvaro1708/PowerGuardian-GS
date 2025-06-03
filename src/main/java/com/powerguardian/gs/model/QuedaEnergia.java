package com.powerguardian.gs.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quedas_energia")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuedaEnergia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id", nullable = false)
    private Setor setor;
    
    @Column(nullable = false)
    private LocalDateTime inicioQueda;
    
    private LocalDateTime fimQueda;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeveridadeQueda severidade;
    
    @Column(nullable = false)
    private Integer pacientesAfetados;
    
    @Column(nullable = false)
    private Integer equipamentosAfetados;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal custoEstimado;
    
    @Column(length = 1000)
    private String observacoes;
    
    @Column(length = 500)
    private String causaProvavel;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusQueda status;
    
    private LocalDateTime previsaoRestauracao;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        if (status == null) {
            status = StatusQueda.ATIVA;
        }
    }
    
    public Long getDuracaoMinutos() {
        if (fimQueda != null) {
            return java.time.Duration.between(inicioQueda, fimQueda).toMinutes();
        }
        return java.time.Duration.between(inicioQueda, LocalDateTime.now()).toMinutes();
    }
    
    public enum SeveridadeQueda {
        BAIXA, MEDIA, ALTA, CRITICA
    }
    
    public enum StatusQueda {
        ATIVA, RESOLVIDA, PARCIALMENTE_RESOLVIDA
    }
}