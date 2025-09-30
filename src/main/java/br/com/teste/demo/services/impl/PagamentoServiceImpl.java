package br.com.teste.demo.services.impl;

import br.com.teste.demo.dtos.PagamentoDTO;
import br.com.teste.demo.enums.FormaPagamento;
import br.com.teste.demo.enums.StatusPagamento;
import br.com.teste.demo.enums.StatusPedido;
import br.com.teste.demo.exceptions.BusinessException;
import br.com.teste.demo.exceptions.ResourceNotFoundException;
import br.com.teste.demo.models.Pagamento;
import br.com.teste.demo.models.Pedido;
import br.com.teste.demo.repositories.PagamentoRepository;
import br.com.teste.demo.repositories.PedidoRepository;
import br.com.teste.demo.services.PagamentoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoServiceImpl(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Override
    @Transactional
    public PagamentoDTO createPagamento(PagamentoDTO pagamentoDTO) {
        Pedido pedido = pedidoRepository.findById(pagamentoDTO.getPedidoId())
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + pagamentoDTO.getPedidoId()));

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setFormaPagamento(pagamentoDTO.getFormaPagamento());
        pagamento.setStatusPagamento(StatusPagamento.PENDENTE);
        pagamento.setValor(pagamentoDTO.getValor() != null ? pagamentoDTO.getValor() : pedido.getValorTotal());
        pagamento.setNumeroParcelas(pagamentoDTO.getNumeroParcelas() != null ? pagamentoDTO.getNumeroParcelas() : 1);
        pagamento.setCodigoTransacao(UUID.randomUUID().toString());
        pagamento.setBandeiraCartao(pagamentoDTO.getBandeiraCartao());
        pagamento.setUltimosDigitosCartao(pagamentoDTO.getUltimosDigitosCartao());
        pagamento.setObservacao(pagamentoDTO.getObservacao());
        pagamento.setDataCadastro(LocalDateTime.now());
        pagamento.setDataAtualizacao(LocalDateTime.now());

        // Define data de vencimento para boleto
        if (pagamentoDTO.getFormaPagamento() == FormaPagamento.BOLETO) {
            pagamento.setDataVencimento(LocalDateTime.now().plusDays(3));
        }

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);
        return PagamentoDTO.fromEntity(pagamentoSalvo);
    }

    @Override
    public PagamentoDTO getPagamentoById(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com id: " + id));
        return PagamentoDTO.fromEntity(pagamento);
    }

    @Override
    public List<PagamentoDTO> getPagamentosByPedidoId(Long pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId).stream()
                .map(PagamentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PagamentoDTO updateStatusPagamento(Long id, StatusPagamento novoStatus) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com id: " + id));

        pagamento.setStatusPagamento(novoStatus);
        pagamento.setDataAtualizacao(LocalDateTime.now());

        if (novoStatus == StatusPagamento.APROVADO) {
            pagamento.setDataPagamento(LocalDateTime.now());
            // Atualiza status do pedido
            Pedido pedido = pagamento.getPedido();
            pedido.setStatusPedido(StatusPedido.PAGO);
            pedido.setDataAtualizacao(LocalDateTime.now());
            pedidoRepository.save(pedido);
        }

        Pagamento pagamentoAtualizado = pagamentoRepository.save(pagamento);
        return PagamentoDTO.fromEntity(pagamentoAtualizado);
    }

    @Override
    @Transactional
    public PagamentoDTO processarPagamento(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com id: " + id));

        if (pagamento.getStatusPagamento() != StatusPagamento.PENDENTE) {
            throw new BusinessException("Apenas pagamentos pendentes podem ser processados");
        }

        // Simula processamento do pagamento
        pagamento.setStatusPagamento(StatusPagamento.PROCESSANDO);
        pagamento.setCodigoAutorizacao(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pagamento.setNsu(String.valueOf(System.currentTimeMillis()).substring(0, 12));
        pagamento.setDataAtualizacao(LocalDateTime.now());

        // Simula aprovação automática (em produção, seria uma integração com gateway de pagamento)
        pagamento.setStatusPagamento(StatusPagamento.APROVADO);
        pagamento.setDataPagamento(LocalDateTime.now());

        // Atualiza status do pedido
        Pedido pedido = pagamento.getPedido();
        pedido.setStatusPedido(StatusPedido.PAGO);
        pedido.setDataAtualizacao(LocalDateTime.now());
        pedidoRepository.save(pedido);

        Pagamento pagamentoProcessado = pagamentoRepository.save(pagamento);
        return PagamentoDTO.fromEntity(pagamentoProcessado);
    }

    @Override
    @Transactional
    public PagamentoDTO cancelarPagamento(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com id: " + id));

        if (pagamento.getStatusPagamento() == StatusPagamento.APROVADO) {
            throw new BusinessException("Pagamentos aprovados não podem ser cancelados. Use estorno.");
        }

        pagamento.setStatusPagamento(StatusPagamento.CANCELADO);
        pagamento.setDataAtualizacao(LocalDateTime.now());

        Pagamento pagamentoCancelado = pagamentoRepository.save(pagamento);
        return PagamentoDTO.fromEntity(pagamentoCancelado);
    }

    @Override
    @Transactional
    public PagamentoDTO estornarPagamento(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado com id: " + id));

        if (pagamento.getStatusPagamento() != StatusPagamento.APROVADO) {
            throw new BusinessException("Apenas pagamentos aprovados podem ser estornados");
        }

        pagamento.setStatusPagamento(StatusPagamento.ESTORNADO);
        pagamento.setDataAtualizacao(LocalDateTime.now());

        // Atualiza status do pedido
        Pedido pedido = pagamento.getPedido();
        pedido.setStatusPedido(StatusPedido.CANCELADO);
        pedido.setDataAtualizacao(LocalDateTime.now());
        pedidoRepository.save(pedido);

        Pagamento pagamentoEstornado = pagamentoRepository.save(pagamento);
        return PagamentoDTO.fromEntity(pagamentoEstornado);
    }

    @Override
    public List<PagamentoDTO> findByFormaPagamento(FormaPagamento formaPagamento) {
        return pagamentoRepository.findByFormaPagamento(formaPagamento).stream()
                .map(PagamentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagamentoDTO> findByStatusPagamento(StatusPagamento statusPagamento) {
        return pagamentoRepository.findByStatusPagamento(statusPagamento).stream()
                .map(PagamentoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PagamentoDTO> findByFiltros(FormaPagamento formaPagamento, StatusPagamento statusPagamento,
                                            LocalDateTime dataInicio, LocalDateTime dataFim) {
        return pagamentoRepository.findByFiltros(formaPagamento, statusPagamento, dataInicio, dataFim).stream()
                .map(PagamentoDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
