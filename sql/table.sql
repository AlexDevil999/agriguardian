create table if not exists app_users
(
    id             serial primary key,
    user_name      text,
    password       text,
    otp            text,
    name           text,
    created_on     timestamp,
    updated_on     timestamp,
    otp_created_on timestamp,
    status         text,
    user_role      text
);

create table if not exists credit_cards
(
    id          serial primary key,
    number      text,
    app_user_id bigint not null,
    foreign key (app_user_id) references app_users (id)
);

create table if not exists subscriptions
(
    id          serial primary key,
    app_user_id bigint not null,
    foreign key (app_user_id) references app_users (id)
);

create table if not exists user_info
(
    id           serial primary key,
    county       text,
    city         text,
    street       text,
    zip_code     text,
    area_code    text,
    phone_code   text,
    phone_number text,
    app_user_id  bigint not null,
    foreign key (app_user_id) references app_users (id)
);

create table if not exists team_groups
(
    id              serial primary key,
    name            text,
    guardian_code   text,
    vulnerable_code text,
    owner_id        bigint not null,
    foreign key (owner_id) references app_users (id)
);

create table if not exists app_user_team_groups
(
    id            serial primary key,
    app_user_id   bigint not null,
    team_group_id bigint not null,
    foreign key (app_user_id) references app_users (id),
    foreign key (team_group_id) references team_groups (id)
);
