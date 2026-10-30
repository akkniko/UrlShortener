CREATE TABLE IF NOT EXISTS links (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    original_url TEXT        NOT NULL,
    code         VARCHAR(32) NOT NULL UNIQUE,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    expire_at    TIMESTAMPTZ
    );

CREATE TABLE IF NOT EXISTS clicks (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    link_id    BIGINT      NOT NULL REFERENCES links(id) ON DELETE CASCADE,
    clicked_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    referrer   TEXT,
    user_agent TEXT
    );

CREATE INDEX IF NOT EXISTS idx_clicks_link_time ON clicks(link_id, clicked_at);