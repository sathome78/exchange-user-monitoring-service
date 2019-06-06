CREATE TABLE IF NOT EXISTS EVENT
(
  id                  INT(40)      UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  email               VARCHAR(255)                        NOT NULL,
  description         VARCHAR(255),
  timestamp           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT IGNORE INTO EVENT (email, description) VALUES ('aaaa@i.ua', 'buy');
