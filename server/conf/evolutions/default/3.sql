# Stats schema
# --- !Ups


CREATE TABLE Stats (
  id BIGINT NOT NULL AUTO_INCREMENT,
  betId BIGINT NOT NULL,
  userId BIGINT NOT NULL,
  invest DOUBLE NOT NULL,
  prediction DOUBLE NOT NULL,
  joinTime BIGINT NOT NULL,
  gain DOUBLE NOT NULL
);

# --- !Downs
DROP TABLE Stats;

