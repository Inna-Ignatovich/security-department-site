package by.bank.security.site.service;

import by.bank.security.site.dto.response.DocumentResponseDto;
import by.bank.security.site.entity.RegulatoryBlock;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RegulatoryDocumentService {

    DocumentResponseDto uploadDocument(RegulatoryBlock block, MultipartFile file, LocalDate date, String name);

    List<DocumentResponseDto> getDocumentsByBlock(RegulatoryBlock block);

    void deleteDocument(RegulatoryBlock block, UUID id);
}