package backend.training.model.enums;

import lombok.Getter;

@Getter
public enum DayOfWeek {
    MONDAY("Segunda"),
    TUESDAY("Terça"),
    WEDNESDAY("Quarta"),
    THURSDAY("Quinta"),
    FRIDAY("Sexta"),
    SATURDAY("Sábado"),
    SUNDAY("Domingo");

    private final String portugueseName;

    DayOfWeek(String portugueseName) {
        this.portugueseName = portugueseName;
    }

    public static DayOfWeek fromString(String value) {
        try {
            return DayOfWeek.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Dia da semana inválido: " + value);
        }
    }
}
