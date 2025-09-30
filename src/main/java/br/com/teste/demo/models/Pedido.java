package br.com.teste.demo.models;

import br.com.teste.demo.enums.StatusPedido;
import br.com.teste.demo.enums.converters.StatusProdutoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "PEDIDO")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NUMERO")
    private Long numero;

    @Column(name = "STATUS")
    @Convert(converter = StatusProdutoConverter.class)
    private StatusPedido statusPedido;

    @Column(name = "SUBTOTAL")
    private BigDecimal subtotal;

    @Column(name = "DESCONTO")
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "FRETE")
    private BigDecimal frete = BigDecimal.ZERO;

    @Column(name = "VALOR_TOTAL")
    private BigDecimal valorTotal;

    @Column(name = "DATA_CADASTRO")
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<Pagamento> pagamentos = new ArrayList<>();
}
