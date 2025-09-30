package br.com.teste.demo.dtos;

import br.com.teste.demo.enums.FormaPagamento;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioFormaPagamentoDTO {

    private FormaPagamento formaPagamento;
    private Long quantidadePagamentos;
    private BigDecimal valorTotal;
    private Double percentualUtilizacao;
}
