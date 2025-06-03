package com.powerguardian.gs.controller;

import com.powerguardian.gs.dto.DashboardResponseDTO;
import com.powerguardian.gs.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Endpoints para dashboard e visão geral do sistema")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Obter dados do dashboard", 
               description = "Retorna dados consolidados para o dashboard principal")
    public ResponseEntity<DashboardResponseDTO> getDashboard() {
        log.info("Requisição recebida para dashboard");
        try {
            var dashboardData = dashboardService.getDashboardData();
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            log.error("Erro ao buscar dados do dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Status rápido do sistema",
               description = "Retorna apenas o status geral do hospital")
    public ResponseEntity<String> getStatus() {
        try {
            var dashboard = dashboardService.getDashboardData();
            return ResponseEntity.ok(dashboard.getStatusGeral().name());
        } catch (Exception e) {
            log.error("Erro ao buscar status: {}", e.getMessage());
            return ResponseEntity.ok("INDISPONIVEL");
        }
    }
}