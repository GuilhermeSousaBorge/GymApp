package backend.payment.port;

import backend.payment.model.entity.Payment;

public interface PaymentCommandPort {

    Payment save(Payment payment);

    Payment update(Payment payment);
}

