package br.com.teste.demo.services;

import br.com.teste.demo.dtos.PagamentoDTO;
import br.com.teste.demo.enums.FormaPagamento;
import br.com.teste.demo.enums.StatusPagamento;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface PagamentoService {

    PagamentoDTO createPagamento(PagamentoDTO pagamentoDTO);

    PagamentoDTO getPagamentoById(Long id);

    List<PagamentoDTO> getPagamentosByPedidoId(Long pedidoId);

    PagamentoDTO updateStatusPagamento(Long id, StatusPagamento novoStatus);

    PagamentoDTO processarPagamento(Long id);

    PagamentoDTO cancelarPagamento(Long id);

    PagamentoDTO estornarPagamento(Long id);

    List<PagamentoDTO> findByFormaPagamento(FormaPagamento formaPagamento);

    List<PagamentoDTO> findByStatusPagamento(StatusPagamento statusPagamento);

    List<PagamentoDTO> findByFiltros(FormaPagamento formaPagamento, StatusPagamento statusPagamento,
                                      LocalDateTime dataInicio, LocalDateTime dataFim);
}
