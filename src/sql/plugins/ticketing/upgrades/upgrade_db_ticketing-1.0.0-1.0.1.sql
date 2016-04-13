--
-- Structure for table ticketing_indexer_action
--

DROP TABLE IF EXISTS ticketing_indexer_action;
CREATE TABLE ticketing_indexer_action (
  id_action INT DEFAULT 0 NOT NULL,
  id_ticket INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL ,
  PRIMARY KEY (id_action)
  );
CREATE INDEX ticketing_id_indexer_task ON ticketing_indexer_action (id_task);

--
-- Add columns for table ticketing_ticket
--

ALTER TABLE ticketing_ticket ADD COLUMN id_assigner_user int default '-1';
ALTER TABLE ticketing_ticket ADD COLUMN id_assigner_unit int default '-1';
