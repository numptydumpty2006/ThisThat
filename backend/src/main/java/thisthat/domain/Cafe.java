package thisthat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Cached Google Place. The {@code googlePlaceId} is the natural key;
 * {@code id} is the surrogate PK we generate.
 */
@Entity
@Table(
    name = "cafes",
    indexes = {
        @Index(name = "idx_cafes_last_fetched_at", columnList = "last_fetched_at DESC"),
        @Index(name = "idx_cafes_rating", columnList = "rating DESC")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "google_place_id", nullable = false, unique = true, length = 255)
    private String googlePlaceId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal lat;

    @Column(name = "lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal lng;

    @Column(name = "rating", precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "price_level")
    private Short priceLevel;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "website", length = 500)
    private String website;

    @Column(name = "photo_url", columnDefinition = "text")
    private String photoUrl;

    /** Weekly schedule + open_now flag from Places API. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "opening_hours_json", columnDefinition = "jsonb")
    private OpeningHours openingHours;

    /** e.g. {cafe, coffee, bakery}. Maps to Postgres TEXT[]. */
    @Column(name = "cuisine_types", columnDefinition = "text[]")
    private List<String> cuisineTypes;

    @Column(name = "last_fetched_at", nullable = false)
    private OffsetDateTime lastFetchedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null)     createdAt = now;
        if (lastFetchedAt == null) lastFetchedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        lastFetchedAt = OffsetDateTime.now();
    }
}