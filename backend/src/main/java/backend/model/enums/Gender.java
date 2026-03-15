package backend.model.enums;

public enum Gender {
    MALE,
    FEMALE,
    OTHER;

    public static Gender fromString(String value) {
        try {
            return Gender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Gênero inválido: " + value);
        }
    }
}
