package by.bank.security.site.controller;

import by.bank.security.site.dto.response.DocumentResponseDto;
import by.bank.security.site.entity.RegulatoryBlock;
import by.bank.security.site.service.RegulatoryDocumentService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/regulatory")
@RequiredArgsConstructor
@NullMarked
public class RegulatoryDocumentController {

    private final RegulatoryDocumentService regulatoryDocumentService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{block}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDto> upload(@PathVariable RegulatoryBlock block,
                                                      @RequestPart("file") MultipartFile file,
                                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Nullable LocalDate date,
                                                      @RequestParam(required = false) String name) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(regulatoryDocumentService.uploadDocument(block, file, date, name));
    }

    @GetMapping("/{block}")
    public ResponseEntity<List<DocumentResponseDto>> getByBlock(@PathVariable RegulatoryBlock block) {
        return ResponseEntity.ok(regulatoryDocumentService.getDocumentsByBlock(block));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{block}/{id}")
    public ResponseEntity<?> delete(@PathVariable RegulatoryBlock block, @PathVariable UUID id) {
        regulatoryDocumentService.deleteDocument(block, id);
        return ResponseEntity.noContent().build();
    }
}