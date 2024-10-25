-- CREATE DATABASE
--create database monsterplay;

-- GRANT ALL PRIVILEGES ON DATABASE dist TO user;
\c monsterdb

-- Drop tables if exits
drop table if exists users cascade;
drop table if exists profile cascade;
drop table if exists scoreboard cascade;
drop table if exists stack cascade;
drop table if exists deck cascade;
drop table if exists offer cascade;
drop table if exists battlehistory cascade;
drop table if exists card cascade;
drop table if exists package_content cascade;
drop table if exists package cascade;
drop table if exists cardtype cascade;
drop table if exists elementtype cascade;
drop table if exists monstercard cascade;
drop table if exists monstertype cascade;
drop table if exists attacktype cascade;


-- create tables
create table users (
    uid serial primary key,
    username varchar (255) unique not null ,
    password varchar (255) not null ,
    token varchar (255) unique not null ,
    coins int not null default 20
);


create table profile (
    uid int primary key ,
    firstname varchar (255),
    lastname varchar (255),
    image bytea
);


create table scoreboard (
    uid int primary key ,
    elo int not null default 100,
    win int not null default 0,
    loss int not null default 0,
    draw int not null default 0
);


create table stack (
    uid int,
    cardid int,
    primary key (uid, cardid)
);


create table deck (
    uid int,
    cardid int,
    boostcount int,
    primary key (uid, cardid)
);


create table offer (
    uid int,
    cardid int,
    ctypeid int,
    etypeid int,
    damage int,
    primary key (uid, cardid)
);


create table battlehistory (
    id serial primary key ,
    uid_a int not null ,
    uid_b int not null ,
    cardid_a int not null ,
    cardid_b int not null ,
    battleid int not null,
    timestamp timestamp not null default current_timestamp
);


create table card (
    cardid serial primary key ,
    ctypeid int not null
);


create table package_content (
    packageid int,
    cardid int,
    primary key (packageid,cardid)
);


create table package (
    packageid serial primary key ,
    coins int not null default 5
);


create table cardtype (
    ctypeid serial primary key ,
    name varchar (255) unique not null ,
    damage int ,
    etypeid int
);


create table elementtype (
    etypeid serial primary key ,
    name varchar (255) unique not null
);


create table monstercard (
    ctypeid int not null ,
    mtypeid int not null ,
    primary key (ctypeid,mtypeid)
);


create table monstertype (
    mtypeid serial primary key ,
    name varchar (255) unique not null ,
    atypeid int not null
);


create table attacktype (
    atypeid serial primary key ,
    name varchar (255) unique not null
);



-- add foreign keys
alter table profile
    add constraint fk_profile_users_uid
        foreign key (uid) references users (uid)
            on delete cascade ;

alter table scoreboard
    add constraint fk_scoreboard_users_uid
        foreign key (uid) references users (uid)
            on delete cascade ;

alter table stack
    add constraint fk_stack_users_uid
        foreign key (uid) references users (uid)
            on delete cascade,
    add constraint fk_stack_card_cardid
        foreign key (cardid) references card (cardid)
            on delete cascade ;

alter table deck
    add constraint fk_deck_users_uid
        foreign key (uid) references users (uid)
            on delete cascade,
    add constraint fk_deck_card_cardid
        foreign key (cardid) references card (cardid)
            on delete cascade ;

alter table offer
    add constraint fk_offer_users_uid
        foreign key (uid) references users (uid)
            on delete cascade,
    add constraint fk_offer_card_cardid
        foreign key (cardid) references card (cardid)
            on delete cascade,
    add constraint fk_offer_cardtype_ctypeid
        foreign key (ctypeid) references cardtype (ctypeid)
            on delete set null,
    add constraint fk_offer_elementtype_etypeid
        foreign key (etypeid) references elementtype (etypeid)
            on delete set null ;

alter table card
    add constraint fk_card_cardtype_ctypeid
        foreign key (ctypeid) references cardtype (ctypeid)
            on delete cascade ;

alter table package_content
    add constraint fk_package_content_package_packageid
        foreign key (packageid) references package (packageid)
            on delete cascade ,
    add constraint fk_package_content_card_cardid
        foreign key (cardid) references card (cardid)
            on delete cascade ;

alter table cardtype
    add constraint fk_cardtype_elementtype_etypeid
        foreign key (etypeid) references elementtype (etypeid)
            on delete cascade ;

alter table monstercard
    add constraint fk_monstercard_cardtype_ctypeid
        foreign key (ctypeid) references cardtype (ctypeid)
            on delete cascade ,
    add constraint fk_monstercard_monsertype_mtypeid
        foreign key (mtypeid) references monstertype (mtypeid)
            on delete cascade ;

alter table monstertype
    add constraint fk_monstertype_attacktype_atypeid
        foreign key (atypeid) references attacktype (atypeid);

alter table battlehistory
    add constraint fk_battlehistory_users_uid_a
        foreign key (uid_a) references users (uid)
            on delete set null ,
    add constraint fk_battlehistory_users_uid_b
        foreign key (uid_b) references users (uid)
            on delete set null ,
    add constraint fk_battlehistory_card_cardid_a
        foreign key (cardid_a) references card (cardid)
            on delete set null ,
    add constraint fk_battlehistory_card_cardid_b
        foreign key (cardid_b) references card (cardid)
            on delete set null ;