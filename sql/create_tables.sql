create table if not exists app_users
(
    id             serial primary key,
    user_name      text,
    password       text,
    otp            text,
    created_on     bigint,
    updated_on     bigint,
    otp_created_on bigint,
    status         text,
    user_role      text,
    refresh_token  text,
    rt_created_on  bigint
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
    id            serial primary key,
    name          text,
    country       text,
    county        text,
    city          text,
    street        text,
    zip_area_code text,
    phone_code    text,
    phone_number  text,
    app_user_id   bigint not null,
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
    group_role    text,
    foreign key (app_user_id) references app_users (id),
    foreign key (team_group_id) references team_groups (id)
);
