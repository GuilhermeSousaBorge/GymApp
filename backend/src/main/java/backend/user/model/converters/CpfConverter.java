package backend.user.model.converters;

import backend.user.model.valueObjects.Cpf;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter()
public class CpfConverter  implements AttributeConverter<Cpf,String> {
    @Override
    public String convertToDatabaseColumn(Cpf cpf) {
        return cpf.getValue();
    }

    @Override
    public Cpf convertToEntityAttribute(String cpf) {
        return new Cpf(cpf);
    }
}
