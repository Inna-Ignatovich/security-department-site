package by.bank.security.site.repository;

import by.bank.security.site.entity.RegulatoryBlock;
import by.bank.security.site.entity.RegulatoryDocument;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с {@link RegulatoryDocument}
 */
@Repository
@NullMarked
public interface RegulatoryDocumentRepository extends JpaRepository<RegulatoryDocument, UUID> {

    /**
     * Поиск документов по блоку с сортировкой по дате документа
     */
    List<RegulatoryDocument> findAllByBlockOrderByDocumentDateDescCreatedAtDesc(RegulatoryBlock block);
}