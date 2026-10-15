package by.bank.security.site.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record DocumentResponseDto(UUID id,
                                  String name,
                                  String type,
                                  LocalDate date,
                                  String size,
                                  String downloadUrl) {
}