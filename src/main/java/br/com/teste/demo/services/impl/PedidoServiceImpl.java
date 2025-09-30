package br.com.teste.demo.services.impl;

import br.com.teste.demo.dtos.ItemPedidoDTO;
import br.com.teste.demo.dtos.PedidoDTO;
import br.com.teste.demo.enums.StatusPedido;
import br.com.teste.demo.models.ItemPedido;
import br.com.teste.demo.models.Pedido;
import br.com.teste.demo.models.Produto;
import br.com.teste.demo.repositories.PedidoRepository;
import br.com.teste.demo.repositories.ProdutoRepository;
import br.com.teste.demo.services.PedidoCalculoService;
import br.com.teste.demo.services.PedidoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoCalculoService pedidoCalculoService;

    public PedidoServiceImpl(PedidoRepository pedidoRepository,
                           ProdutoRepository produtoRepository,
                           PedidoCalculoService pedidoCalculoService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoCalculoService = pedidoCalculoService;
    }

    @Override
    public List<PedidoDTO> getAllOrders() {
        return pedidoRepository.findAll().stream()
                .map(PedidoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoDTO getOrderById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));
        return PedidoDTO.fromEntity(pedido);
    }

    @Override
    @Transactional
    public PedidoDTO createOrder(PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setNumero(new Random().nextLong(100000, 999999));
        pedido.setStatusPedido(StatusPedido.PENDENTE_PAGAMENTO);
        pedido.setDataCadastro(LocalDateTime.now());
        pedido.setDataAtualizacao(LocalDateTime.now());

        // Define desconto e frete manuais se fornecidos
        if (pedidoDTO.getDesconto() != null) {
            pedido.setDesconto(pedidoDTO.getDesconto());
        }
        if (pedidoDTO.getFrete() != null) {
            pedido.setFrete(pedidoDTO.getFrete());
        }

        // Adiciona itens ao pedido
        for (ItemPedidoDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + itemDTO.getProdutoId()));

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setValorUnitario(produto.getValor());
            item.setValorTotal(produto.getValor().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));

            pedido.getItens().add(item);
        }

        // Calcula todos os valores do pedido
        pedidoCalculoService.calcularValoresPedido(pedido);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return PedidoDTO.fromEntity(pedidoSalvo);
    }

    @Override
    @Transactional
    public PedidoDTO updateOrder(Long id, PedidoDTO pedidoDTO) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));

        // Limpa itens existentes
        pedido.getItens().clear();

        // Atualiza desconto e frete manuais se fornecidos
        if (pedidoDTO.getDesconto() != null) {
            pedido.setDesconto(pedidoDTO.getDesconto());
        }
        if (pedidoDTO.getFrete() != null) {
            pedido.setFrete(pedidoDTO.getFrete());
        }

        // Adiciona novos itens
        for (ItemPedidoDTO itemDTO : pedidoDTO.getItens()) {
            Produto produto = produtoRepository.findById(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + itemDTO.getProdutoId()));

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setValorUnitario(produto.getValor());
            item.setValorTotal(produto.getValor().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));

            pedido.getItens().add(item);
        }

        // Recalcula todos os valores do pedido
        pedidoCalculoService.calcularValoresPedido(pedido);
        pedido.setDataAtualizacao(LocalDateTime.now());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return PedidoDTO.fromEntity(pedidoAtualizado);
    }

    @Override
    @Transactional
    public PedidoDTO updateOrderStatus(Long id, StatusPedido status) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));

        pedido.setStatusPedido(status);
        pedido.setDataAtualizacao(LocalDateTime.now());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return PedidoDTO.fromEntity(pedidoAtualizado);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("Pedido não encontrado com id: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    @Override
    public List<PedidoDTO> findByStatus(StatusPedido status) {
        return pedidoRepository.findByStatusPedido(status).stream()
                .map(PedidoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoDTO> findByDateRange(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return pedidoRepository.findByDataCadastroBetween(dataInicio, dataFim).stream()
                .map(PedidoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoDTO> findByValorRange(BigDecimal valorMinimo, BigDecimal valorMaximo) {
        return pedidoRepository.findByValorBetween(valorMinimo, valorMaximo).stream()
                .map(PedidoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PedidoDTO> findByFiltros(StatusPedido status, LocalDateTime dataInicio, LocalDateTime dataFim,
                                         BigDecimal valorMinimo, BigDecimal valorMaximo) {
        return pedidoRepository.findByFiltros(status, dataInicio, dataFim, valorMinimo, valorMaximo).stream()
                .map(PedidoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoDTO recalcularPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com id: " + id));

        // Reseta desconto e frete para recalcular automático
        pedido.setDesconto(null);
        pedido.setFrete(null);

        // Recalcula todos os valores
        pedidoCalculoService.calcularValoresPedido(pedido);
        pedido.setDataAtualizacao(LocalDateTime.now());

        Pedido pedidoAtualizado = pedidoRepository.save(pedido);
        return PedidoDTO.fromEntity(pedidoAtualizado);
    }
}
