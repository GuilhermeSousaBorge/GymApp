package backend.user.model.valueObjects;

import backend.infrastructure.exception.BadRequestException;

import java.util.Objects;

public class Email {

    private final String value;

    public Email(String value) {
        if(value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) throw new BadRequestException("Email inválido");
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
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
