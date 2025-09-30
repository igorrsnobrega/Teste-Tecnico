package br.com.teste.demo.dtos;

import br.com.teste.demo.enums.StatusPedido;
import br.com.teste.demo.models.Pedido;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoDTO {

    private Long id;
    private Long numero;
    private StatusPedido statusPedido;
    private BigDecimal subtotal;
    private BigDecimal desconto;
    private BigDecimal frete;
    private BigDecimal valorTotal;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    private List<ItemPedidoDTO> itens = new ArrayList<>();

    public static PedidoDTO fromEntity(Pedido pedido) {
//        List<ItemPedidoDTO> itensDTO = pedido.getItens() != null
//                ? pedido.getItens().stream()
//                    .map(ItemPedidoDTO::fromEntity)
//                    .collect(Collectors.toList())
//                : new ArrayList<>();

        return new PedidoDTO(
//                pedido.getId(),
//                pedido.getNumero(),
//                pedido.getStatusPedido(),
//                pedido.getSubtotal(),
//                pedido.getDesconto(),
//                pedido.getFrete(),
//                pedido.getValorTotal(),
//                pedido.getDataCadastro(),
//                pedido.getDataAtualizacao(),
//                itensDTO
        );
    }
}
