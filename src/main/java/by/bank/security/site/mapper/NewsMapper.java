package by.bank.security.site.mapper;

import by.bank.security.site.dto.request.NewsRequestDto;
import by.bank.security.site.dto.response.NewsResponseDto;
import by.bank.security.site.dto.response.NewsSearchResultDto;
import by.bank.security.site.entity.FileData;
import by.bank.security.site.entity.News;
import by.bank.security.site.entity.NewsCategory;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "date", source = "publishDate")
    @Mapping(target = "imageFileId", source = "image.id")
    @Mapping(target = "imageUrl", source = "image", qualifiedByName = "imageToUrl")
    NewsResponseDto toDto(@Nullable News entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "publishDate", source = "date")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "image", ignore = true)
    News toEntity(@Nullable NewsRequestDto dto);

    @Named("imageToUrl")
    @Nullable
    default String imageToUrl(@Nullable FileData image) {
        if (image == null) {
            return null;
        }
        return "/files/" + image.getId() + "/download";
    }

    @Nullable
    default Instant mapLocalDateToInstant(@Nullable LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    @Mapping(target = "description", source = "description")
    @Mapping(target = "url", expression = "java(generateUrl(entity.getId()))")
    @Mapping(target = "category", expression = "java(defineCategory(entity.getCategory()))")
    NewsSearchResultDto toSearchDto(@Nullable News entity);

    @Nullable
    default String generateUrl(@Nullable UUID newsId) {
        if (newsId == null) {
            return null;
        }
        return "/news/" + newsId;
    }

    @Nullable
    default String defineCategory(@Nullable NewsCategory category) {
        return category != null ? category.getName() : "Без категории";
    }
}
