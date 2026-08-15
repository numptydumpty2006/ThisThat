-- =============================================================================
-- V1__init.sql
-- Initial schema for the Cafe Finder application.
-- Idempotent within a single migration run; never edit after deploy.
-- =============================================================================

-- Required for gen_random_uuid() on PostgreSQL < 13 (no-op on 13+).
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- -----------------------------------------------------------------------------
-- cafes: Google Places cache. One row per unique place_id.
-- -----------------------------------------------------------------------------
CREATE TABLE cafes (
    id                   UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    google_place_id      VARCHAR(255)    UNIQUE NOT NULL,
    name                 VARCHAR(255)    NOT NULL,
    address              VARCHAR(500),
    lat                  NUMERIC(10, 7)  NOT NULL CHECK (lat BETWEEN -90 AND 90),
    lng                  NUMERIC(10, 7)  NOT NULL CHECK (lng BETWEEN -180 AND 180),
    rating               NUMERIC(2, 1)   CHECK (rating BETWEEN 0 AND 5),
    price_level          SMALLINT        CHECK (price_level BETWEEN 0 AND 4),
    phone                VARCHAR(50),
    website              VARCHAR(500),
    photo_url            TEXT,
    opening_hours_json   JSONB,
    cuisine_types        TEXT[]          NOT NULL DEFAULT '{}',
    last_fetched_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),
    created_at           TIMESTAMPTZ     NOT NULL DEFAULT now(),

    -- A bounding box makes "is the user within X km" trivial at scale.
    CONSTRAINT chk_cafes_lat_lng_present CHECK (lat IS NOT NULL AND lng IS NOT NULL)
);

-- Most cafe reads are by place_id (cache lookup) or by freshness (cleanup).
CREATE INDEX idx_cafes_last_fetched_at  ON cafes (last_fetched_at DESC);
CREATE INDEX idx_cafes_rating           ON cafes (rating DESC) WHERE rating IS NOT NULL;
CREATE INDEX idx_cafes_cuisine_types    ON cafes USING GIN (cuisine_types);

COMMENT ON TABLE  cafes IS 'Cache of Google Places results. Refreshed when stale (>1h) or missing.';
COMMENT ON COLUMN cafes.opening_hours_json IS 'Weekly schedule + open_now flag from Places API.';

-- -----------------------------------------------------------------------------
-- api_keys: server-issued client API keys. Stores hash only.
-- -----------------------------------------------------------------------------
CREATE TABLE api_keys (
    id                UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    key_hash          VARCHAR(255)    UNIQUE NOT NULL,
    key_prefix        VARCHAR(20)     NOT NULL,
    label             VARCHAR(100),
    active            BOOLEAN         NOT NULL DEFAULT true,
    rate_limit_rpm    INTEGER         NOT NULL DEFAULT 60 CHECK (rate_limit_rpm > 0),
    created_at        TIMESTAMPTZ     NOT NULL DEFAULT now(),
    last_used_at      TIMESTAMPTZ,
    revoked_at        TIMESTAMPTZ,

    CONSTRAINT chk_api_keys_revoked_inactive
        CHECK ((revoked_at IS NULL AND active = true) OR revoked_at IS NOT NULL)
);

CREATE INDEX idx_api_keys_active        ON api_keys (active) WHERE active = true;
CREATE INDEX idx_api_keys_last_used_at  ON api_keys (last_used_at DESC);

COMMENT ON COLUMN api_keys.key_hash  IS 'SHA-256 hex of raw key. Raw value is never stored.';
COMMENT ON COLUMN api_keys.key_prefix IS 'First 8 chars of raw key, shown in admin UI only.';

-- -----------------------------------------------------------------------------
-- search_history: audit log of search requests.
-- -----------------------------------------------------------------------------
CREATE TABLE search_history (
    id                  BIGSERIAL       PRIMARY KEY,
    lat                 NUMERIC(10, 7)  NOT NULL CHECK (lat BETWEEN -90 AND 90),
    lng                 NUMERIC(10, 7)  NOT NULL CHECK (lng BETWEEN -180 AND 180),
    radius_m            INTEGER         NOT NULL CHECK (radius_m > 0 AND radius_m <= 50000),
    keyword             VARCHAR(100),
    open_now            BOOLEAN         NOT NULL DEFAULT false,
    min_price           SMALLINT        CHECK (min_price BETWEEN 0 AND 4),
    max_price           SMALLINT        CHECK (max_price BETWEEN 0 AND 4),
    min_rating          NUMERIC(2, 1)   CHECK (min_rating BETWEEN 0 AND 5),
    result_count        INTEGER         NOT NULL DEFAULT 0 CHECK (result_count >= 0),
    cache_hit           BOOLEAN         NOT NULL DEFAULT false,
    client_api_key_id   UUID            REFERENCES api_keys(id) ON DELETE SET NULL,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT chk_search_price_range CHECK (
        min_price IS NULL OR max_price IS NULL OR min_price <= max_price
    )
);

CREATE INDEX idx_search_history_created_at    ON search_history (created_at DESC);
CREATE INDEX idx_search_history_api_key       ON search_history (client_api_key_id);
CREATE INDEX idx_search_history_keyword       ON search_history (keyword) WHERE keyword IS NOT NULL;

COMMENT ON TABLE search_history IS 'Audit log. Useful for analytics and abuse detection.';