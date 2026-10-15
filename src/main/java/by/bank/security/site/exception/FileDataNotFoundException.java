package by.bank.security.site.exception;

import java.util.UUID;

public class FileDataNotFoundException extends RuntimeException {

    public FileDataNotFoundException(String message) {
        super(message);
    }
}
