package by.bank.security.site.specification;

import by.bank.security.site.entity.News;
import jakarta.persistence.criteria.JoinType;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;

@Slf4j
public class NewsItemSpecification {

    public static Specification<@NonNull News> specificationByMonthYear(Integer month, Integer year) {
        return (root, query, cb) -> {
            log.debug("Создание спецификации: month={}, year={}", month, year);
            if (month == null && year == null) {
                return null;
            }
            // На данный момент логика подстановки ТЕКУЩЕГО года, если не пришел в параметрах
            int targetYear = (year != null) ? year : Year.now().getValue();

            Instant start;
            Instant end;

            if (month != null) {
                // Если есть месяц
                YearMonth ym = YearMonth.of(targetYear, month);
                start = ym.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
                end = ym.plusMonths(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            } else {
                // Если только год
                Year y = Year.of(targetYear);
                start = y.atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
                end = y.plusYears(1).atDay(1).atStartOfDay(ZoneOffset.UTC).toInstant();
            }

            return cb.and(
                    cb.greaterThanOrEqualTo(root.get("publishDate"), start),
                    cb.lessThan(root.get("publishDate"), end)
            );
        };
    }

    public static Specification<@NonNull News> specificationByText(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) {
                return cb.conjunction();
            }

            if (query.getResultType() != Long.class) {
                root.fetch("category", JoinType.LEFT);
            }

            String pattern = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("contentHtml")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }
}

