package br.com.teste.demo.enums;

public enum StatusProduto {
    ATIVO(1),
    INATIVO(2),
    PENDENTE(3),
    ESGOTADO(4),
    ARQUIVADO(5);

    private final int id;

    StatusProduto(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static StatusProduto fromId(int id) {
        for (StatusProduto status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Id inválido para Status do Produto: " + id);
    }
}