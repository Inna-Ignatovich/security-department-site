package by.bank.security.site.service;

import by.bank.security.site.dto.response.FileDataResponseDownloadDto;
import by.bank.security.site.entity.FileData;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileDataService {

    FileData uploadFile(MultipartFile file);

    FileDataResponseDownloadDto downloadFile(UUID id);

    void deleteFile(UUID fileId);
}
