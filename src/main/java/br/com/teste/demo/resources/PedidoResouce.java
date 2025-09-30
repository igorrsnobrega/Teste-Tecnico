package br.com.teste.demo.resources;

import br.com.teste.demo.dtos.PedidoDTO;
import br.com.teste.demo.enums.StatusPedido;
import br.com.teste.demo.services.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
@SecurityRequirement(name = "Bearer Authentication")
public class PedidoResouce {

    private final PedidoService pedidoService;

    public PedidoResouce(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    public ResponseEntity<List<PedidoDTO>> getAllOrders() {
        return ResponseEntity.ok(pedidoService.getAllOrders());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    public ResponseEntity<PedidoDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getOrderById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR', 'CLIENTE')")
    public ResponseEntity<PedidoDTO> createOrder(@RequestBody PedidoDTO pedidoDTO) {
        PedidoDTO pedidoCriado = pedidoService.createOrder(pedidoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoCriado);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<PedidoDTO> updateOrder(@PathVariable Long id,
                                                  @RequestBody PedidoDTO pedidoDTO) {
        return ResponseEntity.ok(pedidoService.updateOrder(id, pedidoDTO));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<PedidoDTO> updateOrderStatus(@PathVariable Long id,
                                                        @RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        pedidoService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<PedidoDTO>> filterOrders(
            @RequestParam(required = false) StatusPedido status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim,
            @RequestParam(required = false) BigDecimal valorMinimo,
            @RequestParam(required = false) BigDecimal valorMaximo) {

        return ResponseEntity.ok(pedidoService.findByFiltros(status, dataInicio, dataFim, valorMinimo, valorMaximo));
    }

    @GetMapping("/por-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<PedidoDTO>> findByStatus(@RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.findByStatus(status));
    }

    @GetMapping("/por-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<PedidoDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFim) {
        return ResponseEntity.ok(pedidoService.findByDateRange(dataInicio, dataFim));
    }

    @GetMapping("/por-valor")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<List<PedidoDTO>> findByValorRange(
            @RequestParam BigDecimal valorMinimo,
            @RequestParam BigDecimal valorMaximo) {
        return ResponseEntity.ok(pedidoService.findByValorRange(valorMinimo, valorMaximo));
    }

    @PostMapping("/{id}/recalcular")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")
    public ResponseEntity<PedidoDTO> recalcularPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.recalcularPedido(id));
    }
}
