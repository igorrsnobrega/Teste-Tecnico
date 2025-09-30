package br.com.teste.demo.dtos;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioProdutoDTO {

    private Long produtoId;
    private String produtoTitulo;
    private Integer quantidadeVendida;
    private BigDecimal valorTotalVendido;
    private Integer numeroPedidos;
}
