CREATE SEQUENCE IF NOT EXISTS HIBERNATE_SEQUENCE;

CREATE TABLE IF NOT EXISTS resolution (
    id   integer,
    name varchar(256),
    days integer
);
