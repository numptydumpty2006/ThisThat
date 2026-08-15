package thisthat.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    /** Most recent searches for a given API key (rate-abuse detection). */
    List<SearchHistory> findByClientApiKeyIdOrderByCreatedAtDesc(UUID clientApiKeyId, Pageable page);

    /** Hot path: count searches in the last N minutes. Used by the rate limiter. */
    long countByClientApiKeyIdAndCreatedAtAfter(UUID clientApiKeyId, OffsetDateTime since);

    /** Cache hit-rate metric: total searches vs. cache hits. */
    long countByCacheHitTrue();
}