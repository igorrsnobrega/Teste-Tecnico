package br.com.teste.demo.dtos;

import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.models.Produto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoDTO {

    private Long id;
    private String titulo;
    private String descricao;
    private BigDecimal valor;
    private String categoria;
    private StatusProduto status;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public static ProdutoDTO fromEntity(Produto produto) {
        return new ProdutoDTO(
//                produto.getId(),
//                produto.getTitulo(),
//                produto.getDescricao(),
//                produto.getValor(),
//                produto.getCategoria(),
//                produto.getStatus(),
//                produto.getDataCadastro(),
//                produto.getDataAtualizacao()
        );
    }
}