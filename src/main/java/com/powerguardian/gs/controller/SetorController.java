package com.powerguardian.gs.controller;

import com.powerguardian.gs.dto.SetorDTO;
import com.powerguardian.gs.service.SetorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setores")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Setores", description = "Gerenciamento de setores hospitalares")
@CrossOrigin(origins = "*")
public class SetorController {

    private final SetorService setorService;

    @GetMapping
    @Operation(summary = "Listar todos os setores",
               description = "Retorna lista de todos os setores cadastrados")
    public ResponseEntity<List<SetorDTO>> listarTodos() {
        log.info("Requisição para listar todos os setores");
        try {
            var setores = setorService.listarTodos();
            return ResponseEntity.ok(setores);
        } catch (Exception e) {
            log.error("Erro ao listar setores: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/prioridade")
    @Operation(summary = "Listar setores por prioridade",
               description = "Retorna setores ordenados por nível de prioridade")
    public ResponseEntity<List<SetorDTO>> listarPorPrioridade() {
        log.info("Requisição para listar setores por prioridade");
        try {
            var setores = setorService.listarPorPrioridade();
            return ResponseEntity.ok(setores);
        } catch (Exception e) {
            log.error("Erro ao listar setores por prioridade: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/afetados")
    @Operation(summary = "Listar setores afetados",
               description = "Retorna apenas setores com quedas de energia ativas")
    public ResponseEntity<List<SetorDTO>> listarAfetados() {
        log.info("Requisição para listar setores afetados");
        try {
            var setores = setorService.listarAfetados();
            return ResponseEntity.ok(setores);
        } catch (Exception e) {
            log.error("Erro ao listar setores afetados: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar setor por ID",
               description = "Retorna detalhes de um setor específico")
    public ResponseEntity<SetorDTO> buscarPorId(
            @Parameter(description = "ID do setor") @PathVariable Long id) {
        log.info("Requisição para buscar setor ID: {}", id);
        try {
            var setor = setorService.buscarPorId(id);
            return ResponseEntity.ok(setor);
        } catch (RuntimeException e) {
            log.warn("Setor não encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erro ao buscar setor: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Criar novo setor",
               description = "Cadastra um novo setor hospitalar")
    public ResponseEntity<SetorDTO> criar(@Valid @RequestBody SetorDTO setorDTO) {
        log.info("Requisição para criar setor: {}", setorDTO.getNome());
        try {
            var setorCriado = setorService.criar(setorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(setorCriado);
        } catch (Exception e) {
            log.error("Erro ao criar setor: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar setor",
               description = "Atualiza dados de um setor existente")
    public ResponseEntity<SetorDTO> atualizar(
            @Parameter(description = "ID do setor") @PathVariable Long id,
            @Valid @RequestBody SetorDTO setorDTO) {
        log.info("Requisição para atualizar setor ID: {}", id);
        try {
            var setorAtualizado = setorService.atualizar(id, setorDTO);
            return ResponseEntity.ok(setorAtualizado);
        } catch (RuntimeException e) {
            log.warn("Setor não encontrado para atualização: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erro ao atualizar setor: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar setor",
               description = "Remove um setor do sistema")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do setor") @PathVariable Long id) {
        log.info("Requisição para deletar setor ID: {}", id);
        try {
            setorService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("Setor não encontrado para exclusão: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erro ao deletar setor: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
