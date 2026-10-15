package by.bank.security.site.repository;

import by.bank.security.site.entity.News;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@NullMarked
public interface NewsRepository extends JpaRepository<News, UUID>, JpaSpecificationExecutor<News> {

    @EntityGraph(attributePaths = {"category"})
    List<News> findBy(Limit limit, Sort sort);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE News n SET n.isFeatured = false WHERE n.isFeatured = true")
    void resetFeaturedStatus();

    @EntityGraph(attributePaths = {"category"})
    Optional<News> findById(UUID id);
}