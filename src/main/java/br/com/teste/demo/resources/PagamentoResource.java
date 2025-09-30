package br.com.teste.demo.resources;

import br.com.teste.demo.dtos.PagamentoDTO;
import br.com.teste.demo.enums.FormaPagamento;
import br.com.teste.demo.enums.StatusPagamento;
import br.com.teste.demo.services.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pagamentos")
@Tag(name = "Pagamentos", description = "Endpoints para gerenciamento de pagamentos")
@SecurityRequirement(name = "Bearer Authentication")
public class PagamentoResource {

    private final PagamentoService pagamentoService;

    public PagamentoResource(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    public ResponseEntity<PagamentoDTO> createPagamento(@RequestBody PagamentoDTO pagamentoDTO) {
        PagamentoDTO pagamentoCriado = pagamentoService.createPagamento(pagamentoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoCriado);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    public ResponseEntity<PagamentoDTO> getPagamentoById(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.getPagamentoById(id));
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    public ResponseEntity<List<PagamentoDTO>> getPagamentosByPedidoId(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.getPagamentosByPedidoId(pedidoId));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<PagamentoDTO> updateStatusPagamento(@PathVariable Long id,
                                                               @RequestParam StatusPagamento status) {
        return ResponseEntity.ok(pagamentoService.updateStatusPagamento(id, status));
    }

    @PostMapping("/{id}/processar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<PagamentoDTO> processarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.processarPagamento(id));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<PagamentoDTO> cancelarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.cancelarPagamento(id));
    }

    @PostMapping("/{id}/estornar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagamentoDTO> estornarPagamento(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.estornarPagamento(id));
    }

    @GetMapping("/por-forma-pagamento")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<PagamentoDTO>> findByFormaPagamento(@RequestParam FormaPagamento formaPagamento) {
        return ResponseEntity.ok(pagamentoService.findByFormaPagamento(formaPagamento));
    }

    @GetMapping("/por-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<PagamentoDTO>> findByStatusPagamento(@RequestParam StatusPagamento statusPagamento) {
        return ResponseEntity.ok(pagamentoService.findByStatusPagamento(statusPagamento));
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<PagamentoDTO>> filterPagamentos(
            @RequestParam(required = false) FormaPagamento formaPagamento,
            @RequestParam(required = false) StatusPagamento statusPagamento,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {

        return ResponseEntity.ok(pagamentoService.findByFiltros(formaPagamento, statusPagamento, dataInicio, dataFim));
    }
}
