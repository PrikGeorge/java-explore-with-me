DROP TABLE IF EXISTS hits CASCADE;

CREATE TABLE hits (
    id              INTEGER PRIMARY KEY,
    app             TEXT,
    uri             TEXT,
    ip              TEXT,
    created         TIMESTAMP WITHOUT TIME ZONE
);

CREATE UNIQUE INDEX hits_idx ON hits (id);
