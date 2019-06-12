CREATE TABLE IF NOT EXISTS EVENT
(
  id                  INT(40)      UNSIGNED PRIMARY KEY   NOT NULL AUTO_INCREMENT,
  email               VARCHAR(255)                        NOT NULL,
  description         VARCHAR(255),
  timestamp           TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS EVENT_METADATA
(
    event_id                  INT(40)      UNSIGNED PRIMARY KEY   NOT NULL,
    metadata_json             JSON NOT NULL,
    CONSTRAINT event_metadata_event_id_fk FOREIGN KEY (event_id) REFERENCES EVENT (id)
);

