
    create sequence short_url_SEQ start with 1 increment by 50;

    create sequence users_SEQ start with 1 increment by 50;

    create table short_url (
        clickCount bigint,
        createdAt timestamp(6) not null,
        id bigint not null,
        originalUrl varchar(2048) not null,
        shortCode varchar(255) not null unique,
        primary key (id)
    );

    create table users (
        id bigint not null,
        password varchar(255),
        role varchar(255),
        username varchar(255),
        primary key (id)
    );
