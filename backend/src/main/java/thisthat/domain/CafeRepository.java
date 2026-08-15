package thisthat.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, UUID> {

    /**
     * The cache lookup. 99% of "have we seen this place before?" hits this method.
     */
    Optional<Cafe> findByGooglePlaceId(String googlePlaceId);

    /**
     * Bulk lookup — used after a Google Places Nearby Search returns multiple
     * place_ids. Returns one row per id (missing ids are simply absent).
     */
    List<Cafe> findByGooglePlaceIdIn(List<String> googlePlaceIds);

    boolean existsByGooglePlaceId(String googlePlaceId);

    /**
     * Cleanup helper for a scheduled job: delete cache entries that haven't
     * been refreshed in N days. Returns the row count for logging.
     */
    @Modifying
    @Query("DELETE FROM Cafe c WHERE c.lastFetchedAt < :cutoff")
    int deleteStaleEntries(@Param("cutoff") OffsetDateTime cutoff);

    /**
     * For analytics: top-N cafes by rating, ignoring unrated ones.
     */
    List<Cafe> findTop20ByRatingNotNullOrderByRatingDesc();
}