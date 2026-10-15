package by.bank.security.site.repository;

import by.bank.security.site.entity.NewsCategory;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с {@link NewsCategory}
 */
@Repository
public interface NewsCategoryRepository extends ListCrudRepository<NewsCategory, UUID> {

    /**
     * Поиск категории по названию
     */
    Optional<NewsCategory> findByName(String name);
}