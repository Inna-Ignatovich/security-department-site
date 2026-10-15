package by.bank.security.site.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * ДТО-ответ одного экземпляра новости
 */
public record NewsResponseDto(UUID id,
                              String title,
                              String description,
                              Instant date,
                              String category,
                              UUID imageFileId,
                              String imageUrl,
                              String contentHtml,
                              Boolean isFeatured
) {
}
