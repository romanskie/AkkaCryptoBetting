# User schema
# --- !Ups



CREATE TABLE User (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email varchar NOT NULL,
  password varchar NOT NULL,
  firstName  varchar NOT NULL,
  lastName VARCHAR NOT NULL,
  wallet double NOT NULL
);


INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (1, 'hafti@web.de','123456','Haft','Von Befehl',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (2, 'roman@web.de','123456','Roman','Von Schader',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (3, 'sinksar@web.de','123456','Sinksar','Stiefelus',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (4, 'alex@web.de','123456','Alexander','Der Große',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (5, 'jenny@web.de','123456','Jennifer',' Schreiber',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (6, 'jonas@web.de','123456','Jonas','Zeige',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (7, 'klaus@web.de','123456','Klaus','Maus',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (8, 'franz@web.de','123456','Franz','Ferdinand',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (9, 'kranz@web.de','123456','Rebeka','Kranz',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (10, 'stefan@web.de','123456','Stefan','Von Hichentell',5000);
INSERT INTO User(id,email,password,firstName,lastName,wallet) VALUES (11, 'thomas@web.de','123456','Thomas','Von Zeigeruhr',5000);


# --- !Downs
DROP TABLE user;

