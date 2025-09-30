package br.com.teste.demo.enums.converters;

import br.com.teste.demo.enums.StatusPedido;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusPedidoConverter implements AttributeConverter<StatusPedido, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StatusPedido status) {
        return status != null ? status.getId() : null;
    }

    @Override
    public StatusPedido convertToEntityAttribute(Integer id) {
        return id != null ? StatusPedido.fromId(id) : null;
    }
}
