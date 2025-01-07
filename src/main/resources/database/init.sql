CREATE DATABASE monsterdb;

-- GRANT ALL PRIVILEGES ON DATABASE dist TO user;
\c monsterdb

 CREATE TABLE IF NOT EXISTS users (
    uid serial PRIMARY KEY,
    username VARCHAR (255) UNIQUE NOT NULL,
    password VARCHAR (255) NOT NULL,
    token VARCHAR (255),
    coins INT NOT NULL DEFAULT 20
);

CREATE TABLE IF NOT EXISTS card (
   cid VARCHAR (255) PRIMARY KEY,
   name VARCHAR(255) NOT NULL,
   damage DOUBLE precision NOT NULL,
   element_type VARCHAR(50) NOT NULL,
   monster_type VARCHAR(50)
);
CREATE TABLE IF NOT EXISTS packages (
    pid SERIAL NOT NULL,
    cid VARCHAR (255) NOT NULL,
    PRIMARY KEY (pid, cid),
    FOREIGN KEY (cid) REFERENCES card(cid)
);
CREATE TABLE IF NOT EXISTS stack(
    username VARCHAR (255) NOT NULL,
    cid VARCHAR (255) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (cid) REFERENCES card(cid)
);
CREATE TABLE IF NOT EXISTS deck (
    username VARCHAR(255) NOT NULL,
    cid VARCHAR(255) NOT NULL,
    deck_slot INT NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username),
    FOREIGN KEY (cid) REFERENCES card(cid),
    PRIMARY KEY (username, deck_slot)
);
CREATE TABLE IF NOT EXISTS userprofile (
    username VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255),
    bio TEXT,
    image TEXT,
    FOREIGN KEY (username) REFERENCES users(username)
);
CREATE TABLE IF NOT EXISTS stats (
    username VARCHAR(255) PRIMARY KEY,
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    games_lost INT DEFAULT 0,
    elo INT DEFAULT 100,
    FOREIGN KEY (username) REFERENCES users(username)
);
CREATE TABLE trades (
    trade_id VARCHAR(255) PRIMARY KEY,
    offered_card_id VARCHAR(255) NOT NULL,
    required_type VARCHAR(50) NOT NULL,
    min_damage INT NOT NULL,
    offered_by VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'active',
    FOREIGN KEY (offered_card_id) REFERENCES card(cid),
    FOREIGN KEY (offered_by) REFERENCES users(username)
);