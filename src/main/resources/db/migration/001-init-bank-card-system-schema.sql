--liquibase formatted sql

--changeset author:Koteron failOnError:true
create extension if not exists "uuid-ossp";

create table users (
    id uuid primary key default gen_random_uuid(),
    nickname varchar not null,
    email varchar not null unique,
    password_hash varchar not null,
    role varchar not null check (role in ('ADMIN', 'USER')) default 'user'
);

create table cards (
    id uuid primary key default gen_random_uuid(),
    encrypted_number varchar not null unique,
    owner_id uuid,
    expiration_date date,
    status varchar not null check (status in('ACTIVE', 'LOCKED', 'PENDING_LOCK', 'EXPIRED')),
    balance decimal(19, 2) default 0.0,

    constraint card_owner_id_fk foreign key (owner_id) references users (id) on delete cascade
);

create table transactions (
    id uuid primary key default gen_random_uuid(),
    card_from uuid not null,
    card_to uuid not null,
    amount decimal(19, 2) not null,
    timestamp timestamp not null,

    constraint transaction_card_id_fk foreign key (card_id) references cards(id) on delete cascade
);

-----

-- rollback drop table users;
-- rollback drop table cards;
-- rollback drop table transactions;

