package br.com.teste.demo.enums;

public enum Role {
    ADMIN(1, "ROLE_ADMIN"),
    CLIENTE(2, "ROLE_CLIENTE"),
    OPERADOR(3, "ROLE_OPERADOR");

    private final int id;
    private final String authority;

    Role(int id, String authority) {
        this.id = id;
        this.authority = authority;
    }

    public int getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }

    public static Role fromId(int id) {
        for (Role role : values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        throw new IllegalArgumentException("Id inválido para Role: " + id);
    }
}
