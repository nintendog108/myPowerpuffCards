CREATE DATABASE swen;
\c swen;


CREATE TABLE user (
                      uid serial PRIMARY KEY,
                      username VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      token VARCHAR(255) NOT NULL,
                      coins INT DEFAULT 0
);

CREATE TABLE profile (
                         uid INT PRIMARY KEY,
                         firstname VARCHAR(255),
                         lastname VARCHAR(255),
                         elo INT DEFAULT 0, -- hmmm
                         FOREIGN KEY (uid) REFERENCES user(uid)
);

CREATE TABLE scoreboard (
                            uid INT PRIMARY KEY,
                            elo INT DEFAULT 0,
                            win INT DEFAULT 0,
                            loss INT DEFAULT 0,
                            draw INT DEFAULT 0,
                            FOREIGN KEY (uid) REFERENCES user(uid)
);

CREATE TABLE card (
                      cardid serial PRIMARY KEY,
                      category VARCHAR(255),
                      ctid INT,
                      fieldname VARCHAR(255),
                      fieldname2 VARCHAR(255),
                      fieldname3 VARCHAR(255)
);

CREATE TABLE cardtype (
                          ctid serial PRIMARY KEY,
                          name VARCHAR(255),
                          damage INT,
                          etid INT,
                          FOREIGN KEY (etid) REFERENCES elementtype(etid)
);

CREATE TABLE elementtype (
                             etid serial PRIMARY KEY,
                             name VARCHAR(255),
                             mtid INT,
                             FOREIGN KEY (mtid) REFERENCES monstertype(mtid)
);

CREATE TABLE monstertype (
                             mtid serial PRIMARY KEY,
                             name VARCHAR(255),
                             atid INT,
                             FOREIGN KEY (atid) REFERENCES attacktype(atid)
);

CREATE TABLE attacktype (
                            atid serial PRIMARY KEY,
                            name VARCHAR(255)
);

CREATE TABLE deck (
                      uid INT,
                      cardid INT,
                      boostcount INT DEFAULT 0,
                      PRIMARY KEY (uid, cardid),
                      FOREIGN KEY (uid) REFERENCES user(uid),
                      FOREIGN KEY (cardid) REFERENCES card(cardid)
);

CREATE TABLE stack (
                       uid INT,
                       cardid INT,
                       PRIMARY KEY (uid, cardid),
                       FOREIGN KEY (uid) REFERENCES user(uid),
                       FOREIGN KEY (cardid) REFERENCES card(cardid)
);

CREATE TABLE offer (
                       uid INT,
                       cardid INT,
                       cardtype INT,
                       elementtype INT,
                       damage INT,
                       PRIMARY KEY (uid, cardid),
                       FOREIGN KEY (uid) REFERENCES user(uid),
                       FOREIGN KEY (cardid) REFERENCES card(cardid),
                       FOREIGN KEY (cardtype) REFERENCES cardtype(ctid),
                       FOREIGN KEY (elementtype) REFERENCES elementtype(etid)
);

CREATE TABLE battlehistory (
                               id serial PRIMARY KEY,
                               uid_a INT,
                               uid_b INT,
                               cardid_a INT,
                               cardid_b INT,
                               battleid INT,
                               timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (uid_a) REFERENCES user(uid),
                               FOREIGN KEY (uid_b) REFERENCES user(uid),
                               FOREIGN KEY (cardid_a) REFERENCES card(cardid),
                               FOREIGN KEY (cardid_b) REFERENCES card(cardid)
);

CREATE TABLE package (
                         packageid serial PRIMARY KEY,
                         coins INT
);

CREATE TABLE packaged_card (
                               packageid INT,
                               cardid INT,
                               PRIMARY KEY (packageid, cardid),
                               FOREIGN KEY (packageid) REFERENCES package(packageid),
                               FOREIGN KEY (cardid) REFERENCES card(cardid)
);


INSERT INTO user (username, password, token, coins) VALUES
                                                        ('john_doe', 'password123', 'token_john', 20),
                                                        ('jane_smith', 'securePass456', 'token_jane', 20),
                                                        ('michael_lee', 'myPassword789', 'token_michael', 20),
                                                        ('emily_watson', 'passEmily@321', 'token_emily', 20),
                                                        ('david_jones', 'david!pass987', 'token_david', 20);

