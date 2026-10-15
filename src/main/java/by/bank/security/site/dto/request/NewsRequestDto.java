package by.bank.security.site.dto.request;

import java.time.LocalDate;

/**
 * ДТО-запрос для создания/обновления экземпляра новости
 */
public record NewsRequestDto(String title,
                             String description,
                             LocalDate date,
                             String category,
                             String image,
                             String contentHtml,
                             Boolean isFeatured) {
}
