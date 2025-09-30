package br.com.teste.demo.enums.converters;

import br.com.teste.demo.enums.StatusPagamento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusPagamentoConverter implements AttributeConverter<StatusPagamento, Integer> {

    @Override
    public Integer convertToDatabaseColumn(StatusPagamento statusPagamento) {
        if (statusPagamento == null) {
            return null;
        }
        return statusPagamento.getId();
    }

    @Override
    public StatusPagamento convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return StatusPagamento.fromId(id);
    }
}
