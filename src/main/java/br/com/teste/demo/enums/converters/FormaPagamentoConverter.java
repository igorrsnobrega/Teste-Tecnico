package br.com.teste.demo.enums.converters;

import br.com.teste.demo.enums.FormaPagamento;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FormaPagamentoConverter implements AttributeConverter<FormaPagamento, Integer> {

    @Override
    public Integer convertToDatabaseColumn(FormaPagamento formaPagamento) {
        if (formaPagamento == null) {
            return null;
        }
        return formaPagamento.getId();
    }

    @Override
    public FormaPagamento convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return FormaPagamento.fromId(id);
    }
}
