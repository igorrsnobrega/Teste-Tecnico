package br.com.teste.demo.dtos;

import br.com.teste.demo.enums.FormaPagamento;
import br.com.teste.demo.enums.StatusPagamento;
import br.com.teste.demo.models.Pagamento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagamentoDTO {

    private Long id;
    private Long pedidoId;
    private FormaPagamento formaPagamento;
    private StatusPagamento statusPagamento;
    private BigDecimal valor;
    private Integer numeroParcelas;
    private String codigoTransacao;
    private String codigoAutorizacao;
    private String nsu;
    private String bandeiraCartao;
    private String ultimosDigitosCartao;
    private LocalDateTime dataVencimento;
    private LocalDateTime dataPagamento;
    private String observacao;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public static PagamentoDTO fromEntity(Pagamento pagamento) {
        return new PagamentoDTO(
//                pagamento.getId(),
//                pagamento.getPedido().getId(),
//                pagamento.getFormaPagamento(),
//                pagamento.getStatusPagamento(),
//                pagamento.getValor(),
//                pagamento.getNumeroParcelas(),
//                pagamento.getCodigoTransacao(),
//                pagamento.getCodigoAutorizacao(),
//                pagamento.getNsu(),
//                pagamento.getBandeiraCartao(),
//                pagamento.getUltimosDigitosCartao(),
//                pagamento.getDataVencimento(),
//                pagamento.getDataPagamento(),
//                pagamento.getObservacao(),
//                pagamento.getDataCadastro(),
//                pagamento.getDataAtualizacao()
        );
    }
}
