package backend.training.model.interfaces;

import backend.training.model.enums.DayOfWeek;

import java.util.Set;

public interface TrainingSheetUpdatable {

    String getName();

    String getDescription();

    Integer getRestTimeSeconds();

    Integer getOrderInProgram();

    Set<DayOfWeek> getWeekdays();

    Boolean getActive();
}

