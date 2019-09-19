create table users (
    ID integer generated by default as identity (start with 1),
    EMAIL VARCHAR(256) not null unique,
    PASSWORD_HASH varchar(64) not null,
    DISPLAY_NAME VARCHAR(256),
);

create view rolemap(EMAIL,ROLE) AS
    select EMAIL,'user' as ROLE from users;
