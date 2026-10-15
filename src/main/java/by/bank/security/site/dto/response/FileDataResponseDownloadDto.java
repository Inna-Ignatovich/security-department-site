package by.bank.security.site.dto.response;


import by.bank.security.site.entity.FileData;
import org.springframework.core.io.Resource;

public record FileDataResponseDownloadDto(Resource resource,
                                          FileData metadata) {
}
