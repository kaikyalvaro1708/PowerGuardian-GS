package com.powerguardian.gs.service;

import com.powerguardian.gs.dto.DashboardResponseDTO;
import com.powerguardian.gs.model.StatusHospital;
import com.powerguardian.gs.model.QuedaEnergia;
import com.powerguardian.gs.repository.SetorRepository;
import com.powerguardian.gs.repository.QuedaEnergiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    
    private final SetorRepository setorRepository;
    private final QuedaEnergiaRepository quedaEnergiaRepository;
    
    public DashboardResponseDTO getDashboardData() {
        log.info("Coletando dados do dashboard");
        
        long totalSetores = setorRepository.count();
        long setoresAfetados = setorRepository.countSetoresAfetados();
        long quedaAtivas = quedaEnergiaRepository.countQuedasAtivas();
        Long pacientesAfetados = quedaEnergiaRepository.sumPacientesAfetadosEmQuedasAtivas();
        Double custoTotal = quedaEnergiaRepository.sumCustoEstimadoQuedasAtivas();
        
        StatusHospital.StatusGeral statusGeral = determinarStatusGeral((int) setoresAfetados, (int) totalSetores);
        List<DashboardResponseDTO.EventoRecenteDTO> eventosRecentes = getEventosRecentes();
        DashboardResponseDTO.DadosEnergiaNacional dadosEnergia = getDadosEnergiaNacional();
        
        return DashboardResponseDTO.builder()
                .statusGeral(statusGeral)
                .setoresAfetados((int) setoresAfetados)
                .totalSetores((int) totalSetores)
                .pacientesAfetados(pacientesAfetados != null ? pacientesAfetados.intValue() : 0)
                .quedaAtivas((int) quedaAtivas)
                .custoTotalEstimado(custoTotal != null ? BigDecimal.valueOf(custoTotal) : BigDecimal.ZERO)
                .ultimaAtualizacao(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .eventosRecentes(eventosRecentes)
                .dadosEnergiaNacional(dadosEnergia)
                .build();
    }
    
    private StatusHospital.StatusGeral determinarStatusGeral(int setoresAfetados, int totalSetores) {
        if (setoresAfetados == 0) {
            return StatusHospital.StatusGeral.NORMAL;
        }
        
        double percentualAfetados = (double) setoresAfetados / totalSetores * 100;
        
        if (percentualAfetados > 50) {
            return StatusHospital.StatusGeral.CRITICO;
        } else if (percentualAfetados > 20) {
            return StatusHospital.StatusGeral.ALERTA;
        } else {
            return StatusHospital.StatusGeral.NORMAL;
        }
    }
    
   private List<DashboardResponseDTO.EventoRecenteDTO> getEventosRecentes() {
        List<DashboardResponseDTO.EventoRecenteDTO> eventosRecentes = quedaEnergiaRepository.findAllOrderByCreatedDesc()
                .stream()
                .limit(10)
                .map(this::mapToEventoRecente)
                .collect(Collectors.toList());
        
        return eventosRecentes;
    }
    
    private DashboardResponseDTO.EventoRecenteDTO mapToEventoRecente(QuedaEnergia queda) {
        return DashboardResponseDTO.EventoRecenteDTO.builder()
                .id(queda.getId())
                .setor(queda.getSetor().getNome())
                .tipo("Queda de Energia")
                .descricao("Queda de energia no setor " + queda.getSetor().getNome())
                .timestamp(queda.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .severidade(queda.getSeveridade().name())
                .build();
    }
    
    private DashboardResponseDTO.DadosEnergiaNacional getDadosEnergiaNacional() {
        try {
            return DashboardResponseDTO.DadosEnergiaNacional.builder()
                    .regiao("Sudeste")
                    .statusRede("NORMAL")
                    .cargaAtual(85.5)
                    .fonte("ONS - Operador Nacional do Sistema")
                    .build();
        } catch (Exception e) {
            log.warn("Erro ao buscar dados nacionais de energia: {}", e.getMessage());
            return DashboardResponseDTO.DadosEnergiaNacional.builder()
                    .regiao("N/A")
                    .statusRede("Indisponível")
                    .cargaAtual(0.0)
                    .fonte("Dados não disponíveis")
                    .build();
        }
    }
}