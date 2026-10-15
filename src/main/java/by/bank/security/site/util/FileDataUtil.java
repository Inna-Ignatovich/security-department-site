package by.bank.security.site.util;

import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

public class FileDataUtil {

    public static String getFilename(String name, String originalName) {
        return StringUtils.hasText(name) ? name : originalName;
    }

    public static Instant getDocumentDate(LocalDate date) {
        return date != null ? date.atStartOfDay(ZoneOffset.UTC).toInstant() : Instant.now();
    }

    public static String extractExtensionForType(String originalFilename) {
        return Optional.ofNullable(originalFilename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(originalFilename.lastIndexOf(".") + 1).toUpperCase())
                .orElse("FILE");
    }

    public static String extractExtension(String originalFilename) {
        return Optional.ofNullable(originalFilename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf(".")).toLowerCase())
                .orElse("");
    }
}