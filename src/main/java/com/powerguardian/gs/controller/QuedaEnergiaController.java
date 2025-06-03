package com.powerguardian.gs.controller;

import com.powerguardian.gs.dto.QuedaEnergiaDTO;
import com.powerguardian.gs.dto.ImpactoDTO;
import com.powerguardian.gs.service.QuedaEnergiaService;
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
@RequestMapping("/api/quedas")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quedas de Energia", description = "Gerenciamento de quedas de energia")
@CrossOrigin(origins = "*")
public class QuedaEnergiaController {

    private final QuedaEnergiaService quedaEnergiaService;

    @GetMapping
    @Operation(summary = "Listar todas as quedas",
               description = "Retorna lista de todas as quedas de energia registradas")
    public ResponseEntity<List<QuedaEnergiaDTO>> listarTodas() {
        log.info("Requisição para listar todas as quedas");
        try {
            var quedas = quedaEnergiaService.listarTodas();
            return ResponseEntity.ok(quedas);
        } catch (Exception e) {
            log.error("Erro ao listar quedas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/ativas")
    @Operation(summary = "Listar quedas ativas",
               description = "Retorna apenas quedas de energia ainda não resolvidas")
    public ResponseEntity<List<QuedaEnergiaDTO>> listarAtivas() {
        log.info("Requisição para listar quedas ativas");
        try {
            var quedas = quedaEnergiaService.listarAtivas();
            return ResponseEntity.ok(quedas);
        } catch (Exception e) {
            log.error("Erro ao listar quedas ativas: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/setor/{setorId}")
    @Operation(summary = "Listar quedas por setor",
               description = "Retorna quedas de energia de um setor específico")
    public ResponseEntity<List<QuedaEnergiaDTO>> listarPorSetor(
            @Parameter(description = "ID do setor") @PathVariable Long setorId) {
        log.info("Requisição para listar quedas do setor ID: {}", setorId);
        try {
            var quedas = quedaEnergiaService.listarPorSetor(setorId);
            return ResponseEntity.ok(quedas);
        } catch (Exception e) {
            log.error("Erro ao listar quedas do setor: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar queda por ID",
               description = "Retorna detalhes de uma queda específica")
    public ResponseEntity<QuedaEnergiaDTO> buscarPorId(
            @Parameter(description = "ID da queda") @PathVariable Long id) {
        log.info("Requisição para buscar queda ID: {}", id);
        try {
            var queda = quedaEnergiaService.buscarPorId(id);
            return ResponseEntity.ok(queda);
        } catch (RuntimeException e) {
            log.warn("Queda não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erro ao buscar queda: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @Operation(summary = "Registrar nova queda",
               description = "Registra uma nova queda de energia")
    public ResponseEntity<QuedaEnergiaDTO> registrar(@Valid @RequestBody QuedaEnergiaDTO quedaDTO) {
        log.info("Requisição para registrar nova queda no setor ID: {}", quedaDTO.getSetorId());
        try {
            var quedaRegistrada = quedaEnergiaService.registrar(quedaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(quedaRegistrada);
        } catch (RuntimeException e) {
            log.warn("Erro ao registrar queda: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Erro interno ao registrar queda: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/resolver")
    @Operation(summary = "Resolver queda de energia",
               description = "Marca uma queda como resolvida")
    public ResponseEntity<QuedaEnergiaDTO> resolver(
            @Parameter(description = "ID da queda") @PathVariable Long id) {
        log.info("Requisição para resolver queda ID: {}", id);
        try {
            var quedaResolvida = quedaEnergiaService.resolverQueda(id);
            return ResponseEntity.ok(quedaResolvida);
        } catch (RuntimeException e) {
            log.warn("Queda não encontrada para resolução: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Erro ao resolver queda: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/impacto")
    @Operation(summary = "Calcular impacto das quedas",
               description = "Retorna análise detalhada do impacto das quedas de energia")
    public ResponseEntity<ImpactoDTO> calcularImpacto() {
        log.info("Requisição para calcular impacto das quedas");
        try {
            var impacto = quedaEnergiaService.calcularImpacto();
            return ResponseEntity.ok(impacto);
        } catch (Exception e) {
            log.error("Erro ao calcular impacto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

