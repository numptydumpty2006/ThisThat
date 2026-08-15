package thisthat.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {

    /**
     * Auth lookup. Called on every authenticated request, so it MUST hit
     * the unique index on {@code key_hash}.
     */
    Optional<ApiKey> findByKeyHash(String keyHash);

    /**
     * Touch {@code last_used_at} without loading the full entity first.
     * Saves a SELECT round-trip on every request.
     */
    @Modifying
    @Query("UPDATE ApiKey k SET k.lastUsedAt = :now WHERE k.id = :id")
    int touch(@Param("id") UUID id, @Param("now") OffsetDateTime now);

    boolean existsByKeyHashAndActiveTrue(String keyHash);
}