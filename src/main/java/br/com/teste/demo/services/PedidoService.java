package br.com.teste.demo.services;

import br.com.teste.demo.dtos.PedidoDTO;
import br.com.teste.demo.enums.StatusPedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public interface PedidoService {

    List<PedidoDTO> getAllOrders();

    PedidoDTO getOrderById(Long id);

    PedidoDTO createOrder(PedidoDTO pedidoDTO);

    PedidoDTO updateOrder(Long id, PedidoDTO pedidoDTO);

    PedidoDTO updateOrderStatus(Long id, StatusPedido status);

    void deleteOrder(Long id);

    List<PedidoDTO> findByStatus(StatusPedido status);

    List<PedidoDTO> findByDateRange(LocalDateTime dataInicio, LocalDateTime dataFim);

    List<PedidoDTO> findByValorRange(BigDecimal valorMinimo, BigDecimal valorMaximo);

    List<PedidoDTO> findByFiltros(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim,
                                   BigDecimal valorMinimo, BigDecimal valorMaximo);

    PedidoDTO recalcularPedido(Long id);
}
