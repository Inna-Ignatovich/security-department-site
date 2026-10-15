package by.bank.security.site.exception;

import java.util.UUID;

public class NewsNotFoundException extends RuntimeException {

    public NewsNotFoundException(String message) {
        super(message);
    }
}
