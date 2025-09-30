package br.com.teste.demo.services;

import br.com.teste.demo.dtos.*;
import br.com.teste.demo.enums.FormaPagamento;
import br.com.teste.demo.enums.StatusPedido;
import br.com.teste.demo.models.Pagamento;
import br.com.teste.demo.repositories.ItemPedidoRepository;
import br.com.teste.demo.repositories.PagamentoRepository;
import br.com.teste.demo.repositories.PedidoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PagamentoRepository pagamentoRepository;

    public RelatorioService(PedidoRepository pedidoRepository,
                          ItemPedidoRepository itemPedidoRepository,
                          PagamentoRepository pagamentoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.pagamentoRepository = pagamentoRepository;
    }

    /**
     * Gera relatório de vendas por período customizado
     */
    public RelatorioVendasDTO relatorioVendasPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        RelatorioVendasDTO relatorio = new RelatorioVendasDTO();
        relatorio.setDataInicio(dataInicio);
        relatorio.setDataFim(dataFim);

        Long totalPedidos = pedidoRepository.countPedidosByPeriodo(dataInicio, dataFim);
        relatorio.setTotalPedidos(totalPedidos.intValue());

        BigDecimal valorTotal = pedidoRepository.sumValorTotalByPeriodo(dataInicio, dataFim);
        relatorio.setValorTotalVendas(valorTotal);

        if (totalPedidos > 0) {
            BigDecimal valorMedio = valorTotal.divide(BigDecimal.valueOf(totalPedidos), 2, RoundingMode.HALF_UP);
            relatorio.setValorMedioVendas(valorMedio);
        } else {
            relatorio.setValorMedioVendas(BigDecimal.ZERO);
        }

        BigDecimal totalDescontos = pedidoRepository.sumDescontosByPeriodo(dataInicio, dataFim);
        relatorio.setValorTotalDescontos(totalDescontos);

        BigDecimal totalFrete = pedidoRepository.sumFreteByPeriodo(dataInicio, dataFim);
        relatorio.setValorTotalFrete(totalFrete);

        Integer quantidadeProdutos = pedidoRepository.sumQuantidadeProdutosByPeriodo(dataInicio, dataFim);
        relatorio.setQuantidadeProdutosVendidos(quantidadeProdutos);

        relatorio.setPeriodoDescricao(formatarPeriodo(dataInicio, dataFim));

        return relatorio;
    }

    /**
     * Relatório de vendas diário
     */
    public RelatorioVendasDTO relatorioVendasDiario(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.plusDays(1).atStartOfDay();
        return relatorioVendasPorPeriodo(inicio, fim);
    }

    /**
     * Relatório de vendas mensal
     */
    public RelatorioVendasDTO relatorioVendasMensal(int ano, int mes) {
        LocalDateTime inicio = LocalDate.of(ano, mes, 1).atStartOfDay();
        LocalDateTime fim = inicio.plusMonths(1);
        return relatorioVendasPorPeriodo(inicio, fim);
    }

    /**
     * Relatório de pedidos por status
     */
    public List<RelatorioPorStatusDTO> relatorioPorStatus() {
        Long totalPedidos = pedidoRepository.count();

        return Arrays.stream(StatusPedido.values())
                .map(status -> {
                    Long quantidade = pedidoRepository.countByStatus(status);
                    BigDecimal valorTotal = pedidoRepository.sumValorTotalByStatus(status);

                    Double percentual = 0.0;
                    if (totalPedidos > 0) {
                        percentual = (quantidade.doubleValue() / totalPedidos.doubleValue()) * 100;
                    }

                    return new RelatorioPorStatusDTO(status, quantidade, valorTotal, percentual);
                })
                .collect(Collectors.toList());
    }

    /**
     * Relatório de pedidos por status e período
     */
    public List<RelatorioPorStatusDTO> relatorioPorStatusPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        Long totalPedidos = pedidoRepository.countPedidosByPeriodo(dataInicio, dataFim);

        return Arrays.stream(StatusPedido.values())
                .map(status -> {
                    Long quantidade = pedidoRepository.countByStatusAndPeriodo(status, dataInicio, dataFim);
                    BigDecimal valorTotal = pedidoRepository.sumValorTotalByStatusAndPeriodo(status, dataInicio, dataFim);

                    double percentual = 0.0;
                    if (totalPedidos > 0) {
                        percentual = (quantidade.doubleValue() / totalPedidos.doubleValue()) * 100;
                    }

                    return new RelatorioPorStatusDTO(status, quantidade, valorTotal, percentual);
                })
                .collect(Collectors.toList());
    }

    /**
     * Relatório de produtos mais vendidos
     */
    public List<RelatorioProdutoDTO> relatorioProdutosMaisVendidos(LocalDateTime dataInicio, LocalDateTime dataFim, int limite) {
        List<Object[]> resultados = itemPedidoRepository.findProdutosMaisVendidosByPeriodo(dataInicio, dataFim);

        return resultados.stream()
                .limit(limite)
                .map(row -> new RelatorioProdutoDTO(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        ((Number) row[2]).intValue(),
                        (BigDecimal) row[3],
                        ((Number) row[4]).intValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Relatório de formas de pagamento
     */
    public List<RelatorioFormaPagamentoDTO> relatorioFormasPagamento(LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Object[]> resultados = pagamentoRepository.findByFiltros(null, null, dataInicio, dataFim).stream()
                .collect(Collectors.groupingBy(
                        Pagamento::getFormaPagamento,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> new Object[]{
                                        list.size(),
                                        list.stream()
                                                .map(Pagamento::getValor)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add)
                                }
                        )
                ))
                .entrySet().stream()
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()[0], entry.getValue()[1]})
                .toList();

        long totalPagamentos = resultados.stream()
                .mapToLong(row -> ((Number) row[1]).longValue())
                .sum();

        return resultados.stream()
                .map(row -> {
                    Long quantidade = ((Number) row[1]).longValue();
                    double percentual = 0.0;
                    if (totalPagamentos > 0) {
                        percentual = (quantidade.doubleValue() / (double) totalPagamentos) * 100;
                    }

                    return new RelatorioFormaPagamentoDTO(
                            (FormaPagamento) row[0],
                            quantidade,
                            (BigDecimal) row[2],
                            percentual
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Relatório geral consolidado
     */
    public RelatorioGeralDTO relatorioGeral(LocalDateTime dataInicio, LocalDateTime dataFim) {
        RelatorioGeralDTO relatorio = new RelatorioGeralDTO();
        relatorio.setDataInicio(dataInicio);
        relatorio.setDataFim(dataFim);

        relatorio.setVendas(relatorioVendasPorPeriodo(dataInicio, dataFim));
        relatorio.setPedidosPorStatus(relatorioPorStatusPeriodo(dataInicio, dataFim));
        relatorio.setProdutosMaisVendidos(relatorioProdutosMaisVendidos(dataInicio, dataFim, 10));
        relatorio.setFormasPagamento(relatorioFormasPagamento(dataInicio, dataFim));

        return relatorio;
    }

    private String formatarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return inicio.format(formatter) + " a " + fim.format(formatter);
    }
}
