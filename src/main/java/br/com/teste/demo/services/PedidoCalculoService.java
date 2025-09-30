package br.com.teste.demo.services;

import br.com.teste.demo.models.Pedido;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PedidoCalculoService {

    /**
     * Calcula o subtotal do pedido (soma dos valores dos itens)
     */
    public BigDecimal calcularSubtotal(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            return BigDecimal.ZERO;
        }

        return pedido.getItens().stream()
                .map(item -> item.getValorTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula o desconto baseado em regras de negócio
     * - 10% de desconto para pedidos acima de R$ 1000
     * - 5% de desconto para pedidos acima de R$ 500
     */
    public BigDecimal calcularDesconto(BigDecimal subtotal, BigDecimal descontoManual) {
        if (descontoManual != null && descontoManual.compareTo(BigDecimal.ZERO) > 0) {
            return descontoManual;
        }

        if (subtotal.compareTo(new BigDecimal("1000.00")) >= 0) {
            return subtotal.multiply(new BigDecimal("0.10"));
        } else if (subtotal.compareTo(new BigDecimal("500.00")) >= 0) {
            return subtotal.multiply(new BigDecimal("0.05"));
        }

        return BigDecimal.ZERO;
    }

    /**
     * Calcula o frete baseado em regras de negócio
     * - Frete grátis para pedidos acima de R$ 500 (após desconto)
     * - R$ 20 de frete para pedidos acima de R$ 200
     * - R$ 30 de frete para pedidos abaixo de R$ 200
     */
    public BigDecimal calcularFrete(BigDecimal subtotalComDesconto, BigDecimal freteManual) {
        if (freteManual != null && freteManual.compareTo(BigDecimal.ZERO) >= 0) {
            return freteManual;
        }

        if (subtotalComDesconto.compareTo(new BigDecimal("500.00")) >= 0) {
            return BigDecimal.ZERO;
        } else if (subtotalComDesconto.compareTo(new BigDecimal("200.00")) >= 0) {
            return new BigDecimal("20.00");
        } else {
            return new BigDecimal("30.00");
        }
    }

    /**
     * Calcula todos os valores do pedido (subtotal, desconto, frete, total)
     */
    public void calcularValoresPedido(Pedido pedido) {
        // Calcula subtotal
        BigDecimal subtotal = calcularSubtotal(pedido);
        pedido.setSubtotal(subtotal);

        // Calcula desconto
        BigDecimal desconto = calcularDesconto(subtotal, pedido.getDesconto());
        pedido.setDesconto(desconto);

        // Subtotal com desconto
        BigDecimal subtotalComDesconto = subtotal.subtract(desconto);

        // Calcula frete
        BigDecimal frete = calcularFrete(subtotalComDesconto, pedido.getFrete());
        pedido.setFrete(frete);

        // Calcula valor total
        BigDecimal valorTotal = subtotalComDesconto.add(frete);
        pedido.setValorTotal(valorTotal);
    }
}
