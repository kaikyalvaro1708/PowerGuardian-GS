package com.powerguardian.gs.service;

import com.powerguardian.gs.dto.SetorDTO;
import com.powerguardian.gs.model.Setor;
import com.powerguardian.gs.repository.SetorRepository;
import com.powerguardian.gs.repository.QuedaEnergiaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SetorService {
    
    private final SetorRepository setorRepository;
    private final QuedaEnergiaRepository quedaEnergiaRepository;
    
    public List<SetorDTO> listarTodos() {
        log.info("Listando todos os setores");
        return setorRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SetorDTO> listarPorPrioridade() {
        log.info("Listando setores por prioridade");
        return setorRepository.findAllOrderByPrioridade()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SetorDTO> listarAfetados() {
        log.info("Listando setores afetados");
        return setorRepository.findSetoresComQuedasAtivas()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    public SetorDTO buscarPorId(Long id) {
        log.info("Buscando setor por ID: {}", id);
        var setor = setorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setor não encontrado"));
        return mapToDTO(setor);
    }
    
    @Transactional
    public SetorDTO criar(SetorDTO setorDTO) {
        log.info("Criando novo setor: {}", setorDTO.getNome());
        var setor = mapToEntity(setorDTO);
        setor.setStatus(Setor.StatusSetor.NORMAL);
        var setorSalvo = setorRepository.save(setor);
        return mapToDTO(setorSalvo);
    }
    
    @Transactional
    public SetorDTO atualizar(Long id, SetorDTO setorDTO) {
        log.info("Atualizando setor ID: {}", id);
        var setor = setorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setor não encontrado"));
        
        setor.setNome(setorDTO.getNome());
        setor.setDescricao(setorDTO.getDescricao());
        setor.setCapacidadePacientes(setorDTO.getCapacidadePacientes());
        setor.setEquipamentosCriticos(setorDTO.getEquipamentosCriticos());
        setor.setTemGerador(setorDTO.getTemGerador());
        setor.setNivelPrioridade(setorDTO.getNivelPrioridade());
        
        var setorAtualizado = setorRepository.save(setor);
        return mapToDTO(setorAtualizado);
    }
    
    @Transactional
    public void deletar(Long id) {
        log.info("Deletando setor ID: {}", id);
        if (!setorRepository.existsById(id)) {
            throw new RuntimeException("Setor não encontrado");
        }
        setorRepository.deleteById(id);
    }
    
    private SetorDTO mapToDTO(Setor setor) {
        var quedaAtivas = quedaEnergiaRepository.findBySetorId(setor.getId())
                .stream()
                .mapToInt(q -> q.getStatus().equals(com.powerguardian.gs.model.QuedaEnergia.StatusQueda.ATIVA) ? 1 : 0)
                .sum();
        
        return SetorDTO.builder()
                .id(setor.getId())
                .nome(setor.getNome())
                .descricao(setor.getDescricao())
                .capacidadePacientes(setor.getCapacidadePacientes())
                .equipamentosCriticos(setor.getEquipamentosCriticos())
                .status(setor.getStatus())
                .temGerador(setor.getTemGerador())
                .nivelPrioridade(setor.getNivelPrioridade())
                .criadoEm(setor.getCriadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .atualizadoEm(setor.getAtualizadoEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .quedaAtivas(quedaAtivas)
                .build();
    }
    
    private Setor mapToEntity(SetorDTO dto) {
        return Setor.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .capacidadePacientes(dto.getCapacidadePacientes())
                .equipamentosCriticos(dto.getEquipamentosCriticos())
                .temGerador(dto.getTemGerador())
                .nivelPrioridade(dto.getNivelPrioridade())
                .build();
    }
}
