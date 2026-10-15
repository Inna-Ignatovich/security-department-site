package by.bank.security.site.exception;

import java.util.UUID;

public class RegulatoryDocumentNotFoundException extends RuntimeException {

    public RegulatoryDocumentNotFoundException(String message) {
        super(message);
    }
}