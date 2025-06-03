package com.powerguardian.gs.service;

import com.powerguardian.gs.dto.QuedaEnergiaDTO;
import com.powerguardian.gs.dto.ImpactoDTO;
import com.powerguardian.gs.model.QuedaEnergia;
import com.powerguardian.gs.model.Setor;
import com.powerguardian.gs.repository.QuedaEnergiaRepository;
import com.powerguardian.gs.repository.SetorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuedaEnergiaService {
    
    private final QuedaEnergiaRepository quedaEnergiaRepository;
    private final SetorRepository setorRepository;
    
    public List<QuedaEnergiaDTO> listarTodas() {
        log.info("Listando todas as quedas de energia");
        return quedaEnergiaRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuedaEnergiaDTO> listarAtivas() {
        log.info("Listando quedas ativas");
        return quedaEnergiaRepository.findQuedasAtivasOrderBySeveridade()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<QuedaEnergiaDTO> listarPorSetor(Long setorId) {
        log.info("Listando quedas do setor ID: {}", setorId);
        return quedaEnergiaRepository.findBySetorId(setorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public QuedaEnergiaDTO buscarPorId(Long id) {
        log.info("Buscando queda por ID: {}", id);
        var queda = quedaEnergiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Queda de energia não encontrada"));
        return mapToDTO(queda);
    }
    
    @Transactional
    public QuedaEnergiaDTO registrar(QuedaEnergiaDTO quedaDTO) {
        log.info("Registrando nova queda de energia no setor ID: {}", quedaDTO.getSetorId());
        
        var setor = setorRepository.findById(quedaDTO.getSetorId())
                .orElseThrow(() -> new RuntimeException("Setor não encontrado"));
        
        var queda = mapToEntity(quedaDTO, setor);
        queda.setInicioQueda(LocalDateTime.now());
        
        atualizarStatusSetor(setor, quedaDTO.getSeveridade());
        
        var quedaSalva = quedaEnergiaRepository.save(queda);
        return mapToDTO(quedaSalva);
    }
    
    @Transactional
    public QuedaEnergiaDTO resolverQueda(Long id) {
        log.info("Resolvendo queda ID: {}", id);
        var queda = quedaEnergiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Queda não encontrada"));
        
        queda.setFimQueda(LocalDateTime.now());
        queda.setStatus(QuedaEnergia.StatusQueda.RESOLVIDA);
        
        var outrasQuedasAtivas = quedaEnergiaRepository.findBySetorId(queda.getSetor().getId())
                .stream()
                .anyMatch(q -> !q.getId().equals(id) && q.getStatus() == QuedaEnergia.StatusQueda.ATIVA);
        
        if (!outrasQuedasAtivas) {
            queda.getSetor().setStatus(Setor.StatusSetor.NORMAL);
            setorRepository.save(queda.getSetor());
        }
        
        var quedaResolvida = quedaEnergiaRepository.save(queda);
        return mapToDTO(quedaResolvida);
    }
    
    public ImpactoDTO calcularImpacto() {
        log.info("Calculando impacto das quedas de energia");
        
        var quedasAtivas = quedaEnergiaRepository.findByStatus(QuedaEnergia.StatusQueda.ATIVA);
        var todasQuedas = quedaEnergiaRepository.findAll();
        
        var resumo = calcularResumoImpacto(todasQuedas);
        var impactoPorSetor = calcularImpactoPorSetor(quedasAtivas);
        var analiseFinanceira = calcularAnaliseFinanceira(quedasAtivas);
        var equipamentosCriticos = simularEquipamentosCriticos(quedasAtivas);
        
        return ImpactoDTO.builder()
                .resumoGeral(resumo)
                .impactoPorSetor(impactoPorSetor)
                .analiseFinanceira(analiseFinanceira)
                .equipamentosCriticos(equipamentosCriticos)
                .build();
    }
    
    private void atualizarStatusSetor(Setor setor, QuedaEnergia.SeveridadeQueda severidade) {
        switch (severidade) {
            case CRITICA:
                setor.setStatus(Setor.StatusSetor.CRITICO);
                break;
            case ALTA:
                setor.setStatus(Setor.StatusSetor.ALERTA);
                break;
            default:
                setor.setStatus(Setor.StatusSetor.ALERTA);
        }
        setorRepository.save(setor);
    }
    
    private ImpactoDTO.ResumoImpacto calcularResumoImpacto(List<QuedaEnergia> quedas) {
        var totalPacientes = quedas.stream().mapToInt(QuedaEnergia::getPacientesAfetados).sum();
        var totalEquipamentos = quedas.stream().mapToInt(QuedaEnergia::getEquipamentosAfetados).sum();
        var duracaoMedia = quedaEnergiaRepository.findAverageDurationInMinutes();
        
        String nivelRisco = "BAIXO";
        if (totalPacientes > 100) {
            nivelRisco = "CRÍTICO";
        } else if (totalPacientes > 50) {
            nivelRisco = "ALTO";
        } else if (totalPacientes > 20) {
            nivelRisco = "MÉDIO";
        }
        
        return ImpactoDTO.ResumoImpacto.builder()
                .totalPacientesAfetados(totalPacientes)
                .totalEquipamentosAfetados(totalEquipamentos)
                .totalQuedas(quedas.size())
                .tempoMedioQueda(duracaoMedia != null ? duracaoMedia.longValue() : 0L)
                .nivelRiscoGeral(nivelRisco)
                .build();
    }
    
    private List<ImpactoDTO.ImpactoSetorDTO> calcularImpactoPorSetor(List<QuedaEnergia> quedas) {
        return quedas.stream()
                .collect(Collectors.groupingBy(q -> q.getSetor()))
                .entrySet().stream()
                .map(entry -> {
                    var setor = entry.getKey();
                    var quedasSetor = entry.getValue();
                    
                    var pacientesAfetados = quedasSetor.stream().mapToInt(QuedaEnergia::getPacientesAfetados).sum();
                    var equipamentosAfetados = quedasSetor.stream().mapToInt(QuedaEnergia::getEquipamentosAfetados).sum();
                    var custoTotal = quedasSetor.stream()
                            .filter(q -> q.getCustoEstimado() != null)
                            .map(QuedaEnergia::getCustoEstimado)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    return ImpactoDTO.ImpactoSetorDTO.builder()
                            .nomeSetor(setor.getNome())
                            .pacientesAfetados(pacientesAfetados)
                            .equipamentosAfetados(equipamentosAfetados)
                            .custoEstimado(custoTotal)
                            .status(setor.getStatus().name())
                            .nivelPrioridade(setor.getNivelPrioridade())
                            .build();
                })
                .collect(Collectors.toList());
    }
    
    private ImpactoDTO.AnaliseFinanceira calcularAnaliseFinanceira(List<QuedaEnergia> quedas) {
        var custoTotal = quedas.stream()
                .filter(q -> q.getCustoEstimado() != null)
                .map(QuedaEnergia::getCustoEstimado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        var custoEquipamentos = custoTotal.multiply(BigDecimal.valueOf(0.4));
        var receitaPerdida = custoTotal.multiply(BigDecimal.valueOf(0.3));
        var custoEmergencia = custoTotal.multiply(BigDecimal.valueOf(0.3));
        
        return ImpactoDTO.AnaliseFinanceira.builder()
                .custoEquipamentos(custoEquipamentos)
                .receitaPerdida(receitaPerdida)
                .custoEmergencia(custoEmergencia)
                .custoTotal(custoTotal)
                .build();
    }
    
    private List<ImpactoDTO.EquipamentoCritico> simularEquipamentosCriticos(List<QuedaEnergia> quedas) {
        return quedas.stream()
                .limit(5)
                .map(queda -> ImpactoDTO.EquipamentoCritico.builder()
                        .nome("Equipamento Crítico " + queda.getId())
                        .setor(queda.getSetor().getNome())
                        .status("INATIVO")
                        .pacientesAfetados(queda.getPacientesAfetados())
                        .tempoInatividade(queda.getDuracaoMinutos() + " min")
                        .build())
                .collect(Collectors.toList());
    }
    
    private QuedaEnergiaDTO mapToDTO(QuedaEnergia queda) {
        return QuedaEnergiaDTO.builder()
                .id(queda.getId())
                .setorId(queda.getSetor().getId())
                .nomeSetor(queda.getSetor().getNome())
                .inicioQueda(queda.getInicioQueda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .fimQueda(queda.getFimQueda() != null ? 
                         queda.getFimQueda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : null)
                .severidade(queda.getSeveridade())
                .pacientesAfetados(queda.getPacientesAfetados())
                .equipamentosAfetados(queda.getEquipamentosAfetados())
                .custoEstimado(queda.getCustoEstimado())
                .observacoes(queda.getObservacoes())
                .causaProvavel(queda.getCausaProvavel())
                .status(queda.getStatus())
                .previsaoRestauracao(queda.getPrevisaoRestauracao() != null ?
                                   queda.getPrevisaoRestauracao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : null)
                .criadoEm(queda.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .duracaoMinutos(queda.getDuracaoMinutos())
                .build();
    }
    
    private QuedaEnergia mapToEntity(QuedaEnergiaDTO dto, Setor setor) {
        return QuedaEnergia.builder()
                .setor(setor)
                .severidade(dto.getSeveridade())
                .pacientesAfetados(dto.getPacientesAfetados())
                .equipamentosAfetados(dto.getEquipamentosAfetados())
                .custoEstimado(dto.getCustoEstimado())
                .observacoes(dto.getObservacoes())
                .causaProvavel(dto.getCausaProvavel())
                .build();
    }
}