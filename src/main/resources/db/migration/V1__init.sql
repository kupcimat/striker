CREATE SEQUENCE IF NOT EXISTS HIBERNATE_SEQUENCE;

CREATE TABLE IF NOT EXISTS user (
    id       integer,
    username varchar(256),
    password varchar(256)
);

CREATE TABLE IF NOT EXISTS resolution (
    id   integer,
    name varchar(256),
    days integer
);
