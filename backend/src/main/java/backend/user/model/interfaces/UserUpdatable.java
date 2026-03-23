package backend.user.model.interfaces;

import backend.user.model.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface UserUpdatable {
    String getName();
    String getEmail();
    String getCpf();
    String getPhone();
    Gender getGender();
    LocalDate getBirthDate();
}
