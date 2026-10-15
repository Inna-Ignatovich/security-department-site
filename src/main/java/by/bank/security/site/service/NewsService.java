package by.bank.security.site.service;

import by.bank.security.site.dto.request.NewsRequestDto;
import by.bank.security.site.dto.response.NewsResponseDto;
import by.bank.security.site.dto.response.NewsSearchResultDto;
import by.bank.security.site.dto.response.OffsetListResponseDto;

import java.util.List;
import java.util.UUID;

public interface NewsService {

    NewsResponseDto createNews(NewsRequestDto dto);

    OffsetListResponseDto<NewsResponseDto> getAllNewsSortedPaged(int page, int pageSize, Integer month, Integer year);

    List<NewsResponseDto> getLatestNewsSorted(int limit);

    NewsResponseDto getNewsById(UUID id);

    void deleteById(UUID id);

    NewsResponseDto updateNews(UUID id, NewsRequestDto dto);

    OffsetListResponseDto<NewsSearchResultDto> searchNews(String q, int page, int pageSize);
}
