package thisthat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Subset of the Google Places opening_hours payload we care about.
 * Stored as JSONB in Postgres so we don't need to break it into a child table.
 */
public record OpeningHours(
        @JsonProperty("open_now") Boolean openNow,
        List<String> weekdayText
) {}