create table if not exists hits (
    id              INTEGER PRIMARY KEY,
    app             TEXT,
    uri             TEXT,
    ip              TEXT,
    created         TIMESTAMP WITHOUT TIME ZONE,
    constraint hits_pk primary key (id)
);

CREATE UNIQUE INDEX hits_idx ON hits (id);
