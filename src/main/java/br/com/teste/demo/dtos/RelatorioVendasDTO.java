package br.com.teste.demo.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioVendasDTO {

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Integer totalPedidos;
    private BigDecimal valorTotalVendas;
    private BigDecimal valorMedioVendas;
    private BigDecimal valorTotalDescontos;
    private BigDecimal valorTotalFrete;
    private Integer quantidadeProdutosVendidos;
    private String periodoDescricao;
}
