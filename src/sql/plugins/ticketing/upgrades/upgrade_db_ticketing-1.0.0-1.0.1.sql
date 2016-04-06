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

--
-- Add columns for table ticketing_channel
--

ALTER TABLE ticketing_channel ADD COLUMN icon_font varchar(50) default '';

--
-- Update table ticketing_channel
--
UPDATE ticketing_channel SET icon_font='fa fa-phone'  WHERE id_channel=1;
UPDATE ticketing_channel SET icon_font='fa fa-user'  WHERE id_channel=2;
UPDATE ticketing_channel SET icon_font='fa fa-envelope'  WHERE id_channel=3;
UPDATE ticketing_channel SET icon_font='fa fa-twitter'  WHERE id_channel=4;
UPDATE ticketing_channel SET icon_font='fa fa-facebook'  WHERE id_channel=5;
UPDATE ticketing_channel SET icon_font='fa fa-desktop'  WHERE id_channel=99;
