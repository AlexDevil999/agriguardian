create table if not exists app_users
(
    id             serial primary key,
    user_name      text   unique,
    password       text,
    otp            text,
    created_on     bigint,
    updated_on     bigint,
    otp_created_on bigint,
    status         text,
    user_role      text,
    restrictions   text,
    refresh_token  text  unique,
    fcm_token      text,
    mac_address    text
);

create table if not exists location_data
(
    id                          serial primary key,
    lon                         double precision,
    lat                         double precision,
    last_online                 bigint,
    app_user_id                 bigint not null,
    foreign key (app_user_id)   references app_users (id)
);

create table if not exists app_users_relations
(
    controller_id  bigint not null,
    follower_id    bigint not null,
    relation       text,
    PRIMARY KEY (controller_id, follower_id),
    foreign key (controller_id) references app_users (id),
    foreign key (follower_id) references app_users (id)
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
    avatar        integer,
    app_user_id   bigint not null,
    foreign key (app_user_id) references app_users (id)
    );

create table if not exists team_groups
(
    id              serial primary key,
    name            text,
    guardian_code   text   unique,
    vulnerable_code text   unique,
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

create table if not exists bluetooth_zones
(
    id            serial primary key,
    rule          text,
    app_user_id   bigint not null,
    team_group_id bigint not null,
    name          text,
    foreign key (app_user_id) references app_users (id),
    foreign key (team_group_id) references team_groups (id)
    );

create table if not exists app_user_bluetooth_zones
(
    id                serial primary key,
    app_user_id       bigint not null,
    bluetooth_zone_id bigint not null,
    foreign key (app_user_id) references app_users (id),
    foreign key (bluetooth_zone_id) references bluetooth_zones (id)
    );


create table if not exists geo_zones
(
    id            serial primary key,
    rule          text,
    team_group_id bigint not null,
    center_lon    double precision,
    center_lat    double precision,
    radius        int,
    figure        text,
    name          text,
    foreign key (team_group_id) references team_groups (id)
    );

create table if not exists app_users_relations
(
    controller_id  bigint not null,
    follower_id    bigint not null,
    relation       text,
    PRIMARY KEY (controller_id, follower_id),
    foreign key (controller_id) references app_users (id),
    foreign key (follower_id) references app_users (id)
    );

create table if not exists zone_scheduling_rule
(
    id                  serial primary key,
    day_start           text,
    day_end             text,
    time_start           TIME,
    time_end             TIME,
    time_zone            text,
    schedule_period      text,
    alert_geo_zone_id   bigint not null,
    rule_starts_to_work bigint,

    foreign key (alert_geo_zone_id) references geo_zones (id)
    );

create table if not exists app_user_geo_zones
(
    id                serial primary key,
    app_user_id       bigint not null,
    alert_geo_zone_id bigint not null,
    foreign key (app_user_id) references app_users (id),
    foreign key (alert_geo_zone_id) references geo_zones (id)
    );

create table if not exists borders
(
    id          serial primary key,
    lon         double precision,
    lat         double precision,
    position    int,
    geo_zone_id bigint not null,
    foreign key (geo_zone_id) references geo_zones (id)
    );

create table if not exists registration_code
(
    id          serial primary key,
    registration_code text not null,
    valid_till bigint not null,
    app_user_id bigint not null,
    foreign key (app_user_id) references app_users (id)
    );
