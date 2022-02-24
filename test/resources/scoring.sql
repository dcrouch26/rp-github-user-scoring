-- :name create-db :!
set AUTOCOMMIT = on; drop database scoring; create database scoring;

-- :name create-events-table :! 
CREATE TABLE IF NOT EXISTS events (
  id bigserial PRIMARY KEY,
  "user" varchar NOT NULL,
  repository varchar NOT NULL,
  type varchar NOT NULL,
  created_at timestamp NOT NULL default current_timestamp
)
 
-- :name drop-events-table :! 
DROP TABLE IF EXISTS events

-- :name create-scores-table :! 
CREATE TABLE IF NOT EXISTS scores (
  id bigserial PRIMARY KEY,
  type varchar NOT NULL,
  score integer NOT NULL
)

-- :name drop-scores-table :! 
DROP TABLE IF EXISTS scores;

-- :name populate-scores :!
INSERT INTO scores (type, score)
VALUES ('Push', 5), ('PullRequestReviewComment', 4), ('Watch', 3), ('Create', 2), ('Other', 1);

-- :name insert-event :! :n 
INSERT INTO events ("user", type, repository)
VALUES (:user, :type, :repo);
 
-- :name get-user-events :? :*
SELECT "user", repository, type, created_at FROM events WHERE "user" = :user

-- :name get-repo-events :? :*
SELECT "user", repository, type, created_at FROM events WHERE repository = :repository

-- :name get-user-repo-events :? :*
SELECT "user", repository, type, created_at FROM events WHERE "user" = :user AND repository = :repository

-- :name get-aggregate-scores :* 
SELECT events.user, SUM(COALESCE(scores.score,1)) as points FROM events left join scores on events.type = scores.type group by events.user order by points desc
