package br.com.teste.demo.models;

import br.com.teste.demo.enums.FormaPagamento;
import br.com.teste.demo.enums.StatusPagamento;
import br.com.teste.demo.enums.converters.FormaPagamentoConverter;
import br.com.teste.demo.enums.converters.StatusPagamentoConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PAGAMENTO")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PEDIDO_ID", nullable = false)
    private Pedido pedido;

    @Column(name = "FORMA_PAGAMENTO", nullable = false)
    @Convert(converter = FormaPagamentoConverter.class)
    private FormaPagamento formaPagamento;

    @Column(name = "STATUS_PAGAMENTO", nullable = false)
    @Convert(converter = StatusPagamentoConverter.class)
    private StatusPagamento statusPagamento;

    @Column(name = "VALOR", nullable = false)
    private BigDecimal valor;

    @Column(name = "NUMERO_PARCELAS")
    private Integer numeroParcelas = 1;

    @Column(name = "CODIGO_TRANSACAO")
    private String codigoTransacao;

    @Column(name = "CODIGO_AUTORIZACAO")
    private String codigoAutorizacao;

    @Column(name = "NSU")
    private String nsu;

    @Column(name = "BANDEIRA_CARTAO")
    private String bandeiraCartao;

    @Column(name = "ULTIMOS_DIGITOS_CARTAO")
    private String ultimosDigitosCartao;

    @Column(name = "DATA_VENCIMENTO")
    private LocalDateTime dataVencimento;

    @Column(name = "DATA_PAGAMENTO")
    private LocalDateTime dataPagamento;

    @Column(name = "OBSERVACAO", length = 500)
    private String observacao;

    @Column(name = "DATA_CADASTRO")
    private LocalDateTime dataCadastro;

    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;
}
