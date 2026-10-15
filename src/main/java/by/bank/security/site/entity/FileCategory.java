package by.bank.security.site.entity;

import by.bank.security.site.exception.FileTypeNotAllowedException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum FileCategory {

    NEWS_IMAGE("news", Set.of("image/jpeg", "image/png", "image/webp", "image/gif")),

    DOCUMENT("docs", Set.of("application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document")),

    OTHER("misc", Set.of());

    private static final Set<String> FORBIDDEN_MIME_TYPES = Set.of(
            "application/x-msdownload",
            "application/x-sh",
            "application/x-bat",
            "application/x-ms-shortcut",
            "application/x-php",
            "text/x-php",
            "application/x-javascript",
            "text/javascript",
            "application/x-python-code",
            "text/x-python",
            "application/x-elf",
            "application/java-archive"
    );

    private final String folder;

    private final Set<String> allowedMimeTypes;

    public void validate(String realMimeType) {
        if (FORBIDDEN_MIME_TYPES.contains(realMimeType)) {
            throw new FileTypeNotAllowedException("Файл содержит небезопасный контент типа: " + realMimeType);
        }

        if (!allowedMimeTypes.isEmpty() && !allowedMimeTypes.contains(realMimeType)) {
            throw new FileTypeNotAllowedException("Тип " + realMimeType + " не разрешен для данной категории");
        }
    }
}
