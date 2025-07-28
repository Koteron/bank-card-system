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
    owner_id uuid not null ,
    expiration_date date not null,
    status varchar not null check (status in('ACTIVE', 'LOCKED', 'PENDING_LOCK', 'EXPIRED')),
    currency varchar not null check (currency in('RUB', 'EUR', 'USD')),
    balance decimal(19, 2) default 0.0,

    constraint card_owner_id_fk foreign key (owner_id) references users (id) on delete cascade
);

create table transactions (
    id uuid primary key default gen_random_uuid(),
    card_from uuid,
    card_to uuid,
    amount decimal(19, 2) not null,
    currency_to varchar not null check (currency_to in('RUB', 'EUR', 'USD')),
    currency_from varchar not null check (currency_from in('RUB', 'EUR', 'USD')),
    type varchar not null check ( type in('WITHDRAWAL', 'DEPOSIT', 'TRANSFER')),
    timestamp timestamp not null,

    constraint transaction_card_from_id_fk foreign key (card_from) references cards(id) on delete cascade,
    constraint transaction_card_to_id_fk foreign key (card_to) references cards(id) on delete cascade,

    constraint transaction_card_to_null_on_withdrawal check (type <> 'WITHDRAWAL' or card_to is null),
    constraint transaction_card_from_null_on_deposit check (type <> 'DEPOSIT' or card_from is null)
);

-----

-- rollback drop table users;
-- rollback drop table cards;
-- rollback drop table transactions;

