package backend.model.enums;

public enum Roles {
    ADMINISTRADOR("Administrador"),
    PERSONAL("PersonalTrainer"),
    ALUNO("Aluno");


    private final String role;

    Roles(String role){
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
