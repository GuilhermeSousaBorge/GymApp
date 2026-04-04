package backend.user.model.valueObjects;

import backend.infrastructure.exception.BadRequestException;

import java.util.Objects;

public class Cpf {

    private final String value;

    public Cpf(String value) {
        if(value == null || !value.matches("^(\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2})|(\\d{11})$")) throw new BadRequestException("cpf invalido");
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cpf cpf = (Cpf) o;
        return Objects.equals(value, cpf.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
