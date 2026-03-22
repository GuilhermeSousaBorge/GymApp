package backend.model.valueObjects;

import backend.infrastructure.exception.BadRequestException;

public class Email {

    private final String value;

    public Email(String value) {
        if(value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) throw new BadRequestException("Email inválido");
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
