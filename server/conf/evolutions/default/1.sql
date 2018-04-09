# Bets schema
# --- !Ups

CREATE TABLE bet (
  id BIGINT NOT NULL AUTO_INCREMENT UNIQUE,
  startTime BIGINT NOT NULL,
  stopTime BIGINT NOT NULL,
  duration BIGINT NOT NULL,
  bettingTopic VARCHAR NOT NULL,
    bettingCategory VARCHAR NOT NULL,
  startPriceInScalaCoins DOUBLE PRECISION NOT NULL,
  endPriceInScalaCoins DOUBLE PRECISION NOT NULL,
  prediction DOUBLE PRECISION NOT NULL,
  poolInScalaCoins DOUBLE PRECISION NOT NULL,
  participants VARCHAR NOT NULL,
  running BOOLEAN NOT NULL
);


INSERT INTO bet (
  id,
  startTime,
  stopTime,
  duration,
  bettingTopic,
  bettingCategory,
  startPriceInScalaCoins,
  endPriceInScalaCoins,
  prediction,
  poolInScalaCoins,
  participants,
  running) VALUES (
  0,
  0,
  0,
  30000,
  'Bitcoin',
'Closed',
  300.04,
  280.04,
  3.00,
  500.00,
  '121212,121212122,121212121212',
  false);

# --- !Downs
DROP TABLE bet;