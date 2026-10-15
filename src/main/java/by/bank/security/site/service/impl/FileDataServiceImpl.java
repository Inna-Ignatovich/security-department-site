package by.bank.security.site.service.impl;

import by.bank.security.site.dto.response.FileDataResponseDownloadDto;
import by.bank.security.site.entity.FileCategory;
import by.bank.security.site.entity.FileData;
import by.bank.security.site.exception.FileDataNotFoundException;
import by.bank.security.site.exception.FileDataStorageException;
import by.bank.security.site.repository.FileDataRepository;
import by.bank.security.site.service.FileDataService;
import by.bank.security.site.util.FileDataUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileDataServiceImpl implements FileDataService {

    private final FileDataRepository fileDataRepository;

    @Value("${site.storage.root}")
    private String rootPath;

    @Transactional
    @Override
    public FileData uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не может быть пустым");
        }
        String originalName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String contentType = file.getContentType();
        FileCategory category = determineCategory(contentType);
        category.validate(contentType);

        String dateDir = LocalDate.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String extension = FileDataUtil.extractExtension(originalName);
        String fileName = UUID.randomUUID() + extension;

        String relativePath = String.join("/", category.getFolder(), dateDir, fileName);
        Path absolutePath = Paths.get(rootPath).resolve(relativePath);

        try {
            Files.createDirectories(absolutePath.getParent());
            file.transferTo(absolutePath);

            FileData fileData = FileData.builder()
                    .originalName(originalName)
                    .storagePath(relativePath)
                    .mimeType(contentType)
                    .size(file.getSize())
                    .category(category)
                    .createdAt(Instant.now())
                    .build();

            return fileDataRepository.save(fileData);
        } catch (IOException e) {
            log.error("Критическая ошибка при сохранении файла: {}", originalName, e);
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public FileDataResponseDownloadDto downloadFile(UUID id) {
        FileData fileData = fileDataRepository.findById(id)
                .orElseThrow(() -> new FileDataNotFoundException("Файл с id " + id + " не найден"));

        Path path = Paths.get(rootPath).resolve(fileData.getStoragePath());

        if (!Files.exists(path)) {
            log.error("Файл [{}] не найден по пути [{}]", id, path);
            throw new FileDataStorageException("Файл с id " + id + " физически отсутствует на сервере");
        }

        return new FileDataResponseDownloadDto(new FileSystemResource(path), fileData);
    }

    @Transactional
    @Override
    public void deleteFile(UUID id) {
        FileData fileData = fileDataRepository.findById(id)
                .orElseThrow(() -> new FileDataNotFoundException("Файл с id " + id + " не найден"));

        Path absolutePath = Paths.get(rootPath).resolve(fileData.getStoragePath());
        try {
            Files.deleteIfExists(absolutePath);
            fileDataRepository.delete(fileData);
            log.info("Файл [{}] успешно удален из [{}]", fileData.getOriginalName(),
                    fileData.getStoragePath());
        } catch (IOException e) {
            log.error("Ошибка при удалении файла [{}] с диска [{}]", fileData.getOriginalName(),
                    fileData.getStoragePath(), e);
            throw new RuntimeException("Не удалось удалить файл с диска", e);
        }
    }

    private FileCategory determineCategory(@Nullable String mimeType) {
        if (mimeType == null) {
            return FileCategory.OTHER;
        }

        return Arrays.stream(FileCategory.values())
                .filter(category -> category != FileCategory.OTHER)
                .filter(category -> category.getAllowedMimeTypes().contains(mimeType))
                .findFirst()
                .orElse(FileCategory.OTHER);
    }
}
