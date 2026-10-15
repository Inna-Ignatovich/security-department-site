package by.bank.security.site.dto.response;

import java.util.UUID;

public record NewsSearchResultDto(UUID id,
                                  String title,
                                  String description,
                                  String url,
                                  String category) {
}