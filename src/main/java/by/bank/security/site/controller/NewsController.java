package by.bank.security.site.controller;

import by.bank.security.site.dto.request.NewsRequestDto;
import by.bank.security.site.dto.response.NewsResponseDto;
import by.bank.security.site.dto.response.NewsSearchResultDto;
import by.bank.security.site.dto.response.OffsetListResponseDto;
import by.bank.security.site.service.NewsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@Validated
public class NewsController {

    private final NewsService newsService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<NewsResponseDto> create(@Valid @RequestBody NewsRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newsService.createNews(dto));
    }

    @GetMapping()
    public ResponseEntity<OffsetListResponseDto<NewsResponseDto>> getAll(
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "10") @Min(1) int pageSize,
            @RequestParam(required = false) @Min(1) @Max(12) Integer month,
            @RequestParam(required = false) @Min(1900) @Max(2100) Integer year) {
        return ResponseEntity.ok(newsService.getAllNewsSortedPaged(page, pageSize, month, year));
    }

    @GetMapping("/latest")
    public ResponseEntity<List<NewsResponseDto>> getLatest(
            @RequestParam(defaultValue = "9") @Min(1) @Max(20) int limit) {
        return ResponseEntity.ok(newsService.getLatestNewsSorted(limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseDto> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        newsService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<NewsResponseDto> update(@PathVariable UUID id, @Valid @RequestBody NewsRequestDto dto) {
        return ResponseEntity.ok(newsService.updateNews(id, dto));
    }

    @GetMapping("/search")
    public OffsetListResponseDto<NewsSearchResultDto> search(@RequestParam @NotBlank String q,
                                                             @RequestParam(defaultValue = "1") @Min(1) int page,
                                                             @RequestParam(defaultValue = "10") @Min(1) int pageSize) {
        return newsService.searchNews(q, page, pageSize);
    }

}
