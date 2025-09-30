package br.com.teste.demo.enums.converters;

import br.com.teste.demo.enums.StatusProduto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StatusProdutoConverter implements AttributeConverter<StatusProduto, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StatusProduto status) {
        return status != null ? status.getId() : null;
    }

    @Override
    public StatusProduto convertToEntityAttribute(Integer id) {
        return id != null ? StatusProduto.fromId(id) : null;
    }
}
