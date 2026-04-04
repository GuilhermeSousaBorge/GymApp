package backend.user.model.converters;

import backend.user.model.valueObjects.Cpf;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter()
public class CpfConverter  implements AttributeConverter<Cpf,String> {
    @Override
    public String convertToDatabaseColumn(Cpf cpf) {
        if(cpf == null) return null;
        return cpf.getValue();
    }

    @Override
    public Cpf convertToEntityAttribute(String cpf) {
        if(cpf == null) return null;
        return new Cpf(cpf);
    }
}
