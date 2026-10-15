package by.bank.security.site.service.impl;

import by.bank.security.site.adapter.Base64MultipartFileAdapter;
import by.bank.security.site.dto.request.NewsRequestDto;
import by.bank.security.site.dto.response.NewsResponseDto;
import by.bank.security.site.dto.response.NewsSearchResultDto;
import by.bank.security.site.dto.response.OffsetListResponseDto;
import by.bank.security.site.entity.FileData;
import by.bank.security.site.entity.News;
import by.bank.security.site.entity.NewsCategory;
import by.bank.security.site.exception.NewsNotFoundException;
import by.bank.security.site.mapper.NewsMapper;
import by.bank.security.site.repository.NewsCategoryRepository;
import by.bank.security.site.repository.NewsRepository;
import by.bank.security.site.service.FileDataService;
import by.bank.security.site.service.NewsService;
import by.bank.security.site.specification.NewsItemSpecification;
import by.bank.security.site.util.HtmlSanitizerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;

    private final NewsCategoryRepository categoryRepository;

    private final NewsMapper newsMapper;

    private final FileDataService fileDataService;

    @Transactional
    @Override
    public NewsResponseDto createNews(NewsRequestDto dto) {
        log.debug("Создание новой новости c названием [{}]", dto.title());
        if (Boolean.TRUE.equals(dto.isFeatured())) {
            newsRepository.resetFeaturedStatus();
        }
        String finalHtml = HtmlSanitizerUtil.sanitize(dto.contentHtml());

        News news = newsMapper.toEntity(dto);
        news.setContentHtml(finalHtml);

        if (dto.image() != null && dto.image().startsWith("data:image")) {
            String fileName = dto.title().replaceAll("[^a-zA-Z0-9а-яА-Я]", "_") + ".jpg";
            FileData savedImage = saveBase64(dto.image(), fileName);
            news.setImage(savedImage);
        }

        setCategoryWithSaveIfAbsent(dto, news);

        News saved = newsRepository.save(news);
        log.info("Новость сохранена с id [{}]", saved.getId());
        return newsMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    @Override
    public OffsetListResponseDto<NewsResponseDto> getAllNewsSortedPaged(int page, int pageSize,
                                                                        Integer month, Integer year) {
        log.debug("Запрос списка новостей: page={}, pageSize={}, month={}, year={}", page, pageSize, month, year);
        Specification<News> specification = NewsItemSpecification.specificationByMonthYear(month, year);

        int springPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(
                springPage, pageSize, Sort.by("isFeatured", "publishDate", "id").descending());

        Page<News> pageResult = newsRepository.findAll(specification, pageable);
        List<NewsResponseDto> content = pageResult.getContent().stream()
                .map(newsMapper::toDto)
                .toList();

        return new OffsetListResponseDto<>(content, pageResult.getTotalElements(), page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public List<NewsResponseDto> getLatestNewsSorted(int limit) {
        log.debug("Запрос списка последних новостей: limit={}", limit);
        List<News> news = newsRepository.findBy(
                Limit.of(limit),
                Sort.by("isFeatured", "publishDate", "id").descending()
        );
        return news.stream()
                .map(newsMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public NewsResponseDto getNewsById(UUID id) {
        log.debug("Получение новости с id [{}]", id);
        return newsRepository.findById(id)
                .map(newsMapper::toDto)
                .orElseThrow(() -> new NewsNotFoundException("Новость с id " + id + " не найдена"));
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Удаление новости с id [{}]", id);
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("Новость с id " + id + " не найдена"));

        FileData image = news.getImage();
        newsRepository.delete(news);

        if (image != null) {
            fileDataService.deleteFile(image.getId());
            log.info("Обложка новости с id [{}] удалена", id);
        }
        log.info("Новость с id [{}] удалена", id);
    }

    @Transactional
    @Override
    public NewsResponseDto updateNews(UUID id, NewsRequestDto dto) {
        log.info("Обновление новости с id [{}]", id);
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException("Новость с id " + id + " не найдена"));

        if (Boolean.TRUE.equals(dto.isFeatured())) {
            newsRepository.resetFeaturedStatus();
        }

        news.setTitle(dto.title());
        news.setDescription(dto.description());
        news.setPublishDate(dto.date().atStartOfDay(ZoneOffset.UTC).toInstant());
        news.setIsFeatured(dto.isFeatured());
        String finalHtml = HtmlSanitizerUtil.sanitize(dto.contentHtml());
        news.setContentHtml(finalHtml);

        String newImageUrl = dto.image();
        FileData currentImage = news.getImage();
        if (newImageUrl != null && newImageUrl.startsWith("data:image")) {
            FileData newImage = saveBase64(newImageUrl, dto.title() + ".jpg");
            news.setImage(newImage);
            if (currentImage != null) {
                fileDataService.deleteFile(currentImage.getId());
            }
        } else if (newImageUrl == null && currentImage != null) {
            news.setImage(null);
            fileDataService.deleteFile(currentImage.getId());
        }

        setCategoryWithSaveIfAbsent(dto, news);

        News updated = newsRepository.save(news);
        log.info("Новость с id [{}] обновлена", updated.getId());
        return newsMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    @Override
    public OffsetListResponseDto<NewsSearchResultDto> searchNews(String q, int page, int pageSize) {
        log.debug("Запрос поиска по новостям: q={}, page={}, pageSize={}", q, page, pageSize);

        int springPage = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(springPage, pageSize, Sort.by("publishDate").descending());

        Page<News> newsPage = newsRepository.findAll(NewsItemSpecification.specificationByText(q), pageable);

        List<NewsSearchResultDto> results = newsPage.getContent().stream()
                .map(newsMapper::toSearchDto)
                .toList();

        return new OffsetListResponseDto<>(
                results,
                newsPage.getTotalElements(),
                page,
                pageSize
        );
    }

    private void setCategoryWithSaveIfAbsent(NewsRequestDto dto, News newsItem) {
        if (dto.category() != null) {
            NewsCategory category = categoryRepository.findByName(dto.category())
                    .orElseGet(() -> categoryRepository.save(
                            NewsCategory.builder().name(dto.category()).build()
                    ));
            newsItem.setCategory(category);
        }
    }

    private FileData saveBase64(String base64String, String originalName) {
        if (!base64String.contains(",")) {
            throw new IllegalArgumentException("Некорректный формат Base64");
        }

        String[] parts = base64String.split(",");
        String header = parts[0];
        String content = parts[1];

        String mimeType = header.substring(header.indexOf(":") + 1, header.indexOf(";"));
        byte[] bytes = Base64.getDecoder().decode(content);

        return fileDataService.uploadFile(new Base64MultipartFileAdapter(bytes, originalName, mimeType));
    }
}
