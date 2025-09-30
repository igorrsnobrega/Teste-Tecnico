package br.com.teste.demo.dtos;

import br.com.teste.demo.models.ItemPedido;
import lombok.*;

import java.math.BigDecimal;

@Data
public class ItemPedidoDTO {

    private Long id;
    private Long produtoId;
    private String produtoTitulo;
    private Integer quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    public ItemPedidoDTO() {
    }

    public ItemPedidoDTO(Long produtoId, Integer quantidade) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }

    public ItemPedidoDTO(Long id, Long produtoId, String produtoTitulo, Integer quantidade,
                         BigDecimal valorUnitario, BigDecimal valorTotal) {
        this.id = id;
        this.produtoId = produtoId;
        this.produtoTitulo = produtoTitulo;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.valorTotal = valorTotal;
    }

    public static ItemPedidoDTO fromEntity(ItemPedido item) {
        if (item == null) {
            return null;
        }

        ItemPedidoDTO dto = new ItemPedidoDTO();
//        dto.setId(item.getId());
//        dto.setProdutoId(item.getProduto() != null ? item.getProduto().getId() : null);
//        dto.setProdutoTitulo(item.getProduto() != null ? item.getProduto().getTitulo() : null);
//        dto.setQuantidade(item.getQuantidade());
//        dto.setValorUnitario(item.getValorUnitario());
//        dto.setValorTotal(item.getValorTotal());
        return dto;
    }
}
