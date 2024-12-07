CREATE DATABASE monsterdb;

-- GRANT ALL PRIVILEGES ON DATABASE dist TO user;
\c monsterdb

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

CREATE TABLE IF NOT EXISTS users (
                                     uid serial PRIMARY KEY,
                                     username VARCHAR (255) UNIQUE NOT NULL,
                                     password VARCHAR (255) NOT NULL,
                                     token VARCHAR (255),
                                     coins INT NOT NULL DEFAULT 20
);

CREATE TABLE card (
                      cid VARCHAR (255) PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      damage INT NOT NULL,
                      element_type VARCHAR(50) NOT NULL,
                      monster_type VARCHAR(50)
);
CREATE TABLE packages (
                          pid SERIAL PRIMARY KEY,
                          cid VARCHAR (255) NOT NULL,
                          FOREIGN KEY (cid) REFERENCES card(cid)
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
