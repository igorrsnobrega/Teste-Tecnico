package br.com.teste.demo.enums;

public enum FormaPagamento {
    CARTAO_CREDITO(1, "Cartão de Crédito"),
    CARTAO_DEBITO(2, "Cartão de Débito"),
    BOLETO(3, "Boleto Bancário"),
    TRANSFERENCIA_BANCARIA(4, "Transferência Bancária"),
    PIX(5, "PIX");

    private final int id;
    private final String descricao;

    FormaPagamento(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public static FormaPagamento fromId(int id) {
        for (FormaPagamento forma : values()) {
            if (forma.getId() == id) {
                return forma;
            }
        }
        throw new IllegalArgumentException("Id inválido para Forma de Pagamento: " + id);
    }
}
