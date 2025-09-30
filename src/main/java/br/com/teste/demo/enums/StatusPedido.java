package br.com.teste.demo.enums;

public enum StatusPedido {
    PAGO(1),
    PENDENTE_PAGAMENTO(2),
    CANCELADO(3);

    private final int id;

    StatusPedido(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static StatusPedido fromId(int id) {
        for (StatusPedido status : values()) {
            if (status.getId() == id) {
                return status;
            }
        }
        throw new IllegalArgumentException("Id inválido para Status do Pedido: " + id);
    }
}