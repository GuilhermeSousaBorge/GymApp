package backend.plan.model.valueObject;

import java.math.BigDecimal;

public class Money {

    private final BigDecimal value;

    public Money(BigDecimal value){
        if(value.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Valor monetário não pode ser negativo");
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }
}
