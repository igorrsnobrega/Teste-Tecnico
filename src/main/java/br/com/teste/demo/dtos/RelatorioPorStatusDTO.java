package br.com.teste.demo.dtos;

import br.com.teste.demo.enums.StatusPedido;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioPorStatusDTO {

    private StatusPedido status;
    private Long quantidadePedidos;
    private BigDecimal valorTotal;
    private Double percentualPedidos;
}
