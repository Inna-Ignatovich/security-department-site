package by.bank.security.site.mapper;

import by.bank.security.site.dto.response.DocumentResponseDto;
import by.bank.security.site.entity.RegulatoryDocument;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegulatoryDocumentMapper {

    @Mapping(target = "type", source = "file.mimeType")
    @Mapping(target = "size", source = "file.size")
    @Mapping(target = "date", expression = "java(toLocalDate(entity.getDocumentDate()))")
    @Mapping(target = "downloadUrl", expression = "java(generateUrl(entity.getFile() != null ? entity.getFile().getId() : null))")
    DocumentResponseDto toDto(@Nullable RegulatoryDocument entity);

    default LocalDate toLocalDate(Instant documentDate) {
        return LocalDate.ofInstant(documentDate, ZoneId.systemDefault());
    }

    @Nullable
    default String generateUrl(@Nullable UUID fileId) {
        if (fileId == null) {
            return null;
        }
        return "/files/" + fileId + "/download";
    }
}