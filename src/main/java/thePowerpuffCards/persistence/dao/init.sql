-- CREATE DATABASE
--create database monsterplay;

-- GRANT ALL PRIVILEGES ON DATABASE dist TO user;
\c monsterdb
drop table if exists users cascade;
drop table if exists profile cascade;
drop table if exists scoreboard cascade;
drop table if exists stack cascade;
drop table if exists deck cascade;
drop table if exists offer cascade;
drop table if exists battlehistory cascade;
drop table if exists card cascade;
drop table if exists package cascade;

-- Erstellen der Tabelle user
CREATE TABLE users (
                      uid SERIAL PRIMARY KEY,
                      username VARCHAR(255) UNIQUE NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      coins INT NOT NULL DEFAULT 20,
                      token VARCHAR(255) UNIQUE
);

-- Erstellen der Tabelle profile
CREATE TABLE profile (
                         uid INT PRIMARY KEY REFERENCES users(uid) ON DELETE CASCADE,
                         firstname VARCHAR(255),
                         lastname VARCHAR(255),
                         image BYTEA
);

-- Erstellen der Tabelle scoreboard
CREATE TABLE scoreboard (
                            uid INT PRIMARY KEY REFERENCES users(uid) ON DELETE CASCADE,
                            elo INT DEFAULT 1000,
                            win INT DEFAULT 0,
                            loss INT DEFAULT 0,
                            draw INT DEFAULT 0
);

CREATE TABLE packages (
                          pid SERIAL PRIMARY KEY
);

-- Tabelle für Cards
CREATE TABLE card (
                      cid SERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      damage INT NOT NULL,
                      element_type VARCHAR(50) NOT NULL,
                      monster_type VARCHAR(50),
                      package_id INT,
                      FOREIGN KEY (package_id) REFERENCES packages (pid) ON DELETE SET NULL
);


-- Erstellen der Tabelle deck
CREATE TABLE deck (
                      uid INT REFERENCES users(uid) ON DELETE CASCADE,
                      cid INT REFERENCES card(cid) ON DELETE CASCADE,
                      PRIMARY KEY (uid, cid)
);

-- Erstellen der Tabelle stack
CREATE TABLE stack (
                       uid INT REFERENCES users(uid) ON DELETE CASCADE,
                       cid INT REFERENCES card(cid) ON DELETE CASCADE,
                       PRIMARY KEY (uid, cid)
);

-- Erstellen der Tabelle offer
CREATE TABLE offer (
                       uid INT REFERENCES users(uid) ON DELETE CASCADE,
                       cardid INT REFERENCES card(cid) ON DELETE CASCADE,
                       cardtype VARCHAR(50),
                       elementtyp VARCHAR(50),
                       damage INT,
                       PRIMARY KEY (uid, cardid)
);

-- Erstellen der Tabelle battlehistory
CREATE TABLE battlehistory (
                               id SERIAL PRIMARY KEY,
                               uid_a INT REFERENCES users(uid) ON DELETE CASCADE,
                               uid_b INT REFERENCES users(uid) ON DELETE CASCADE,
                               cardid_a INT REFERENCES card(cid) ON DELETE CASCADE,
                               cardid_b INT REFERENCES card(cid) ON DELETE CASCADE,
                               battleid INT NOT NULL,
                               timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
/*
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
*/