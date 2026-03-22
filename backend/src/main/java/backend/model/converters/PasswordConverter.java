package backend.model.converters;

import backend.model.valueObjects.Password;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter()
public class PasswordConverter implements AttributeConverter<Password, String> {
    @Override
    public String convertToDatabaseColumn(Password password) {
        return password.getHash();
    }

    @Override
    public Password convertToEntityAttribute(String value) {
        return Password.fromHash(value);
    }
}
