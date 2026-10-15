package by.bank.security.site.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RegulatoryBlock {

    DOCUMENTS("documents"),

    LOCAL_DOCUMENTS("local-documents"),

    USEFUL_INFO("useful-info"),

    RECOMMENDATIONS("recommendations"),

    CORRUPTION_PREVENTION("corruption-prevention");

    private final String value;

    RegulatoryBlock(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RegulatoryBlock fromValue(String value) {
        for (RegulatoryBlock b : RegulatoryBlock.values()) {
            if (b.value.equalsIgnoreCase(value)) return b;
        }
        throw new IllegalArgumentException("Неизвестный блок документов: " + value);
    }
}