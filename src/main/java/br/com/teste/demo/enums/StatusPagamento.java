package br.com.teste.demo.enums;

public enum StatusPagamento {
    PENDENTE(1, "Pendente"),
    PROCESSANDO(2, "Processando"),
    APROVADO(3, "Aprovado"),
    RECUSADO(4, "Recusado"),
    CANCELADO(5, "Cancelado"),
    ESTORNADO(6, "Estornado");

    private final int id;
    private final String descricao;

    StatusPagamento(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusPagamento fromId(int id) {
        for (StatusPagamento status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Id inválido para Status de Pagamento: " + id);
    }
}
