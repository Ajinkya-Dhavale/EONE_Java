-- Ensure plpgsql extension present (Rails enables this by default)
CREATE EXTENSION IF NOT EXISTS plpgsql;

-- ActiveStorage-compatible tables to mirror Rails schema (for parity only)
-- These tables are not used by the Java app but keep the DB structure aligned.

CREATE TABLE IF NOT EXISTS active_storage_blobs (
    id BIGSERIAL PRIMARY KEY,
    key VARCHAR NOT NULL,
    filename VARCHAR NOT NULL,
    content_type VARCHAR,
    metadata TEXT,
    service_name VARCHAR NOT NULL,
    byte_size BIGINT NOT NULL,
    checksum VARCHAR,
    created_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS index_active_storage_blobs_on_key
    ON active_storage_blobs (key);

CREATE TABLE IF NOT EXISTS active_storage_attachments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    record_type VARCHAR NOT NULL,
    record_id BIGINT NOT NULL,
    blob_id BIGINT NOT NULL REFERENCES active_storage_blobs(id),
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS index_active_storage_attachments_on_blob_id
    ON active_storage_attachments (blob_id);

CREATE UNIQUE INDEX IF NOT EXISTS index_active_storage_attachments_uniqueness
    ON active_storage_attachments (record_type, record_id, name, blob_id);

CREATE TABLE IF NOT EXISTS active_storage_variant_records (
    id BIGSERIAL PRIMARY KEY,
    blob_id BIGINT NOT NULL REFERENCES active_storage_blobs(id),
    variation_digest VARCHAR NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS index_active_storage_variant_records_uniqueness
    ON active_storage_variant_records (blob_id, variation_digest);

