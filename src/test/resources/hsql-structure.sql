-- Database initialization script (TEST)
-- This script is supposed to be run on every integration test of the application
-- It is supposed to clear all existing data and re-create the structure

SET DATABASE SQL SYNTAX PGS TRUE; -- enable the use of UNIX_MILLIS() with DEFAULT

-- Start hibernate_do Table ----

DROP TABLE IF EXISTS public.hibernate_do CASCADE;
CREATE TABLE public.hibernate_do
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1),
    created BIGINT DEFAULT UNIX_MILLIS(),
    modified BIGINT DEFAULT UNIX_MILLIS(),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    example_string_property varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE public.hibernate_do IS 'Table for storing Hibernate example DOs';
-- End hibernate_do Table ----