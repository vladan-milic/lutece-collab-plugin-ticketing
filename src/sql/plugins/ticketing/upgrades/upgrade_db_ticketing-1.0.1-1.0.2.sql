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

--
-- add inactive field to avoid physical deletion of records in reference tables
--
ALTER TABLE ticketing_contact_mode ADD COLUMN inactive int(1) NOT NULL default '0';
ALTER TABLE ticketing_ticket_category ADD COLUMN inactive int(1) NOT NULL default '0';
ALTER TABLE ticketing_ticket_domain ADD COLUMN inactive int(1) NOT NULL default '0';
ALTER TABLE ticketing_ticket_type ADD COLUMN inactive int(1) NOT NULL default '0';
ALTER TABLE ticketing_user_title ADD COLUMN inactive int(1) NOT NULL default '0';
ALTER TABLE ticketing_channel ADD COLUMN inactive int(1) NOT NULL default '0';
ALTER TABLE ticketing_ticket_form ADD COLUMN inactive int(1) NOT NULL default '0';
