package br.com.teste.demo.resources;

import br.com.teste.demo.dtos.*;
import br.com.teste.demo.services.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios de vendas e pedidos")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
public class RelatorioResource {

    private final RelatorioService relatorioService;

    public RelatorioResource(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    /**
     * Relatório de vendas diário
     * GET /relatorios/vendas/diario?data=2025-01-15
     */
    @GetMapping("/vendas/diario")
    public ResponseEntity<RelatorioVendasDTO> relatorioVendasDiario(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        return ResponseEntity.ok(relatorioService.relatorioVendasDiario(data));
    }

    /**
     * Relatório de vendas mensal
     * GET /relatorios/vendas/mensal?ano=2025&mes=1
     */
    @GetMapping("/vendas/mensal")
    public ResponseEntity<RelatorioVendasDTO> relatorioVendasMensal(
            @RequestParam int ano,
            @RequestParam int mes) {
        return ResponseEntity.ok(relatorioService.relatorioVendasMensal(ano, mes));
    }

    /**
     * Relatório de vendas por período customizado
     * GET /relatorios/vendas/periodo?dataInicio=2025-01-01T00:00:00&dataFim=2025-01-31T23:59:59
     */
    @GetMapping("/vendas/periodo")
    public ResponseEntity<RelatorioVendasDTO> relatorioVendasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(relatorioService.relatorioVendasPorPeriodo(dataInicio, dataFim));
    }

    /**
     * Relatório de pedidos por status (todos os pedidos)
     * GET /relatorios/pedidos/por-status
     */
    @GetMapping("/pedidos/por-status")
    public ResponseEntity<List<RelatorioPorStatusDTO>> relatorioPorStatus() {
        return ResponseEntity.ok(relatorioService.relatorioPorStatus());
    }

    /**
     * Relatório de pedidos por status e período
     * GET /relatorios/pedidos/por-status/periodo?dataInicio=2025-01-01T00:00:00&dataFim=2025-01-31T23:59:59
     */
    @GetMapping("/pedidos/por-status/periodo")
    public ResponseEntity<List<RelatorioPorStatusDTO>> relatorioPorStatusPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(relatorioService.relatorioPorStatusPeriodo(dataInicio, dataFim));
    }

    /**
     * Relatório de produtos mais vendidos
     * GET /relatorios/produtos/mais-vendidos?dataInicio=2025-01-01T00:00:00&dataFim=2025-01-31T23:59:59&limite=10
     */
    @GetMapping("/produtos/mais-vendidos")
    public ResponseEntity<List<RelatorioProdutoDTO>> relatorioProdutosMaisVendidos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(relatorioService.relatorioProdutosMaisVendidos(dataInicio, dataFim, limite));
    }

    /**
     * Relatório de formas de pagamento
     * GET /relatorios/formas-pagamento?dataInicio=2025-01-01T00:00:00&dataFim=2025-01-31T23:59:59
     */
    @GetMapping("/formas-pagamento")
    public ResponseEntity<List<RelatorioFormaPagamentoDTO>> relatorioFormasPagamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(relatorioService.relatorioFormasPagamento(dataInicio, dataFim));
    }

    /**
     * Relatório geral consolidado
     * GET /relatorios/geral?dataInicio=2025-01-01T00:00:00&dataFim=2025-01-31T23:59:59
     */
    @GetMapping("/geral")
    public ResponseEntity<RelatorioGeralDTO> relatorioGeral(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(relatorioService.relatorioGeral(dataInicio, dataFim));
    }

    /**
     * Relatório do mês atual
     * GET /relatorios/mes-atual
     */
    @GetMapping("/mes-atual")
    public ResponseEntity<RelatorioGeralDTO> relatorioMesAtual() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.withDayOfMonth(1).atStartOfDay();
        LocalDateTime fim = hoje.plusMonths(1).withDayOfMonth(1).atStartOfDay();
        return ResponseEntity.ok(relatorioService.relatorioGeral(inicio, fim));
    }

    /**
     * Relatório de hoje
     * GET /relatorios/hoje
     */
    @GetMapping("/hoje")
    public ResponseEntity<RelatorioVendasDTO> relatorioHoje() {
        return ResponseEntity.ok(relatorioService.relatorioVendasDiario(LocalDate.now()));
    }
}
