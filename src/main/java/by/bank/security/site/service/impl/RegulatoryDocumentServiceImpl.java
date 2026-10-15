package by.bank.security.site.service.impl;

import by.bank.security.site.dto.response.DocumentResponseDto;
import by.bank.security.site.entity.FileData;
import by.bank.security.site.entity.RegulatoryBlock;
import by.bank.security.site.entity.RegulatoryDocument;
import by.bank.security.site.exception.RegulatoryDocumentNotFoundException;
import by.bank.security.site.mapper.RegulatoryDocumentMapper;
import by.bank.security.site.repository.RegulatoryDocumentRepository;
import by.bank.security.site.service.FileDataService;
import by.bank.security.site.service.RegulatoryDocumentService;
import by.bank.security.site.util.FileDataUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegulatoryDocumentServiceImpl implements RegulatoryDocumentService {

    private final RegulatoryDocumentRepository regulatoryDocumentRepository;

    private final FileDataService fileDataService;

    private final RegulatoryDocumentMapper regulatoryDocumentMapper;

    @Transactional
    @Override
    public DocumentResponseDto uploadDocument(RegulatoryBlock block, MultipartFile file,
                                              LocalDate date, String name) {
        FileData fileData = fileDataService.uploadFile(file);

        RegulatoryDocument document = RegulatoryDocument.builder()
                .block(block)
                .name(FileDataUtil.getFilename(name, fileData.getOriginalName()))
                .documentDate(FileDataUtil.getDocumentDate(date))
                .file(fileData)
                .build();

        RegulatoryDocument saved = regulatoryDocumentRepository.save(document);
        log.info("Документ [{}] успешно сохранен в [{}]", saved.getName(), fileData.getStoragePath());
        return regulatoryDocumentMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DocumentResponseDto> getDocumentsByBlock(RegulatoryBlock block) {
        log.debug("Запрос документов из блока [{}]", block.getValue());
        return regulatoryDocumentRepository.findAllByBlockOrderByDocumentDateDescCreatedAtDesc(block)
                .stream()
                .map(regulatoryDocumentMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteDocument(RegulatoryBlock block, UUID id) {
        log.debug("Получен запрос на удаление из блока [{}] документа с id [{}]", block.getValue(), id);
        RegulatoryDocument document = regulatoryDocumentRepository.findById(id)
                .orElseThrow(() -> new RegulatoryDocumentNotFoundException("Нормативный документ с id " + id + " не найден"));

        if (document.getBlock() != block) {
            throw new IllegalArgumentException("Документ " + id + " не принадлежит блоку " + block);
        }
        UUID fileId = document.getFile().getId();
        regulatoryDocumentRepository.delete(document);
        fileDataService.deleteFile(fileId);
        log.info("Файл [{}] c id [{}] успешно удален", document.getName(), id);
    }
}