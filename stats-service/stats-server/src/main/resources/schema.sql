DROP TABLE IF EXISTS hits CASCADE;

CREATE TABLE hits
(
    id      SERIAL PRIMARY KEY,
    app     TEXT,
    uri     TEXT,
    ip      TEXT,
    created TIMESTAMP WITHOUT TIME ZONE
);
