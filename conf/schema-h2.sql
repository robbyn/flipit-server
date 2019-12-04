create table users (
    ID serial,
    EMAIL varchar(256) not null unique,
    PASSWORD_HASH varchar(64) not null,
    DISPLAY_NAME varchar(256),
    primary key (ID)
);

create view rolemap(EMAIL,ROLE) AS
    select EMAIL,'user' as ROLE from users;

create table facets (
    USER_ID integer not null,
    NUMBER integer not null,
    SYMBOL varchar(16),
    LABEL varchar(16),
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
