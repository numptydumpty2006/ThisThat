package thisthat.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A server-issued client API key. We store only the SHA-256 hash.
 *
 * <p>The raw key is generated once, returned to the client at creation time,
 * and never persisted. Authentication filters recompute the hash from the
 * incoming {@code X-API-Key} header and look up by {@code keyHash}.
 */
@Entity
@Table(
    name = "api_keys",
    indexes = {
        @Index(name = "idx_api_keys_last_used_at", columnList = "last_used_at DESC")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "key_hash", nullable = false, unique = true, length = 255)
    private String keyHash;

    @Column(name = "key_prefix", nullable = false, length = 20)
    private String keyPrefix;

    @Column(name = "label", length = 100)
    private String label;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "rate_limit_rpm", nullable = false)
    private int rateLimitRpm;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_used_at")
    private OffsetDateTime lastUsedAt;

    @Column(name = "revoked_at")
    private OffsetDateTime revokedAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }

    public boolean isUsable() {
        return active && revokedAt == null;
    }
}