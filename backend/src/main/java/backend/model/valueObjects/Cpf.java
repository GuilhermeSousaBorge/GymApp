package backend.model.valueObjects;

import backend.infrastructure.exception.BadRequestException;

public class Cpf {

    private final String value;

    public Cpf(String value) {
        if(value == null || !value.matches("^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})|(\\d{11})$")) throw new BadRequestException("cpf invalido");
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
