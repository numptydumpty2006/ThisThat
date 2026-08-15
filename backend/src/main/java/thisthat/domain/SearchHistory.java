package thisthat.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Audit record of one search request. We don't read these on the hot path —
 * only analytics and rate-abuse detection touch them.
 */
@Entity
@Table(
    name = "search_history",
    indexes = {
        @Index(name = "idx_search_history_created_at", columnList = "created_at DESC"),
        @Index(name = "idx_search_history_api_key",    columnList = "client_api_key_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(name = "lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal lng;

    @Column(name = "radius_m", nullable = false)
    private int radiusMeters;

    @Column(name = "keyword", length = 100)
    private String keyword;

    @Column(name = "open_now", nullable = false)
    private boolean openNow;

    @Column(name = "min_price")
    private Short minPrice;

    @Column(name = "max_price")
    private Short maxPrice;

    @Column(name = "min_rating", precision = 2, scale = 1)
    private BigDecimal minRating;

    @Column(name = "result_count", nullable = false)
    private int resultCount;

    @Column(name = "cache_hit", nullable = false)
    private boolean cacheHit;

    @Column(name = "client_api_key_id")
    private UUID clientApiKeyId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}