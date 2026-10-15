package by.bank.security.site.dto.response;

import java.util.List;

public record OffsetListResponseDto<T>(List<T> data,
                                       long total,
                                       int page,
                                       int pageSize) {
}