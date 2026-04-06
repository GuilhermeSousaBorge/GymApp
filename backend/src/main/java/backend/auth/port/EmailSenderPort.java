package backend.auth.port;

public interface EmailSenderPort {

    void send(String to, String subject, String body);
}

