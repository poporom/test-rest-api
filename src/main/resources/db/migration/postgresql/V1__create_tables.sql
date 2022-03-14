create sequence user_id_seq start with 1 increment by 1;
create sequence role_id_seq start with 1 increment by 1;
create sequence message_id_seq start with 1 increment by 1;

create table users (
    id bigint DEFAULT nextval('user_id_seq') not null,
    username varchar(255) not null CONSTRAINT user_username_unique UNIQUE,
    password varchar(255) not null,
    name varchar(255) not null,
    email varchar(255) not null CONSTRAINT user_email_unique UNIQUE,
    enabled boolean not null,
    last_password_reset_date timestamp,
    primary key (id)
);

create table roles (
    id bigint DEFAULT nextval('role_id_seq') not null,
    name varchar(255) not null CONSTRAINT role_name_unique UNIQUE,
    primary key (id)
);

create table user_role (
    user_id bigint REFERENCES users(id),
    role_id bigint REFERENCES roles(id)
);

create table messages (
                          id bigint DEFAULT nextval('message_id_seq') not null,
                          user_id bigint REFERENCES users(id),
                          message varchar(255) not null,
                          primary key (id)
);