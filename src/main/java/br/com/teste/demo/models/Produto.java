package br.com.teste.demo.models;

import br.com.teste.demo.enums.StatusProduto;
import br.com.teste.demo.enums.converters.StatusProdutoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "PRODUTO")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name = "VALOR")
    private BigDecimal valor;

    @Column(name = "STATUS")
    @Convert(converter = StatusProdutoConverter.class)
    private StatusProduto status;

    @Column(name = "DATA_CADASTRO")
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    @Column(name = "CATEGORIA")
    private String categoria;

    @OneToMany(mappedBy = "produto")
    private List<ItemPedido> itens = new ArrayList<>();
}
