package br.com.teste.demo.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioGeralDTO {

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private RelatorioVendasDTO vendas;
    private List<RelatorioPorStatusDTO> pedidosPorStatus = new ArrayList<>();
    private List<RelatorioProdutoDTO> produtosMaisVendidos = new ArrayList<>();
    private List<RelatorioFormaPagamentoDTO> formasPagamento = new ArrayList<>();
}
