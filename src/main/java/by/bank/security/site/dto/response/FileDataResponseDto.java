package by.bank.security.site.dto.response;

import java.util.UUID;

public record FileDataResponseDto(UUID id,
                                  String name,
                                  String mimeType,
                                  Long size,
                                  String downloadUrl) {
}

