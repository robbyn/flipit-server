create table users (
    ID integer generated by default as identity (start with 1),
    EMAIL VARCHAR(256) not null unique,
    PASSWORD_HASH varchar(64) not null,
    DISPLAY_NAME VARCHAR(256),
);

create view rolemap(EMAIL,ROLE) AS
    select EMAIL,'user' as ROLE from users;

create table facets (
    USER_ID integer not null,
    NUMBER integer not null,
    SYMBOL VARCHAR(16),
    LABEL VARCHAR(16),
    primary key (USER_ID, NUMBER),
    foreign key (USER_ID) references users(ID)
);

create table activities (
    USER_ID integer not null,
    START_TIME timestamp not null,
    FACET_NUMBER integer not null,
    COMMENT text,
    primary key (USER_ID,START_TIME),
    foreign key (USER_ID) references users(ID)
);
