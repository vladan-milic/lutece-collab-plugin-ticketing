-- ----------------------------------------------------------
-- Adding Marker field and marking configuration
-- ----------------------------------------------------------

DROP TABLE IF EXISTS ticketing_markings;
CREATE TABLE ticketing_markings (
	id_marking INT(6) NOT NULL,
	title VARCHAR(500) NOT NULL DEFAULT '',
	label_color VARCHAR(50),
	background_color VARCHAR(50),
	PRIMARY KEY (id_marking)
);
INSERT INTO ticketing_markings VALUES (1, 'NON LU', '#FFFFFF', '#00C0EF');


ALTER TABLE ticketing_ticket ADD COLUMN	id_marking INT(11) DEFAULT '0';
UPDATE ticketing_ticket SET id_marking = 1 WHERE is_read = 0;
ALTER TABLE ticketing_ticket DROP COLUMN is_read;

DELETE FROM core_user_right WHERE id_right = 'TICKETING_MANAGEMENT_MARKING';

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT_MARKING';
SET @id_order_max := (SELECT MAX(id_order) FROM core_admin_right WHERE id_feature_group = 'APPLICATIONS') + 1;
INSERT INTO core_admin_right VALUES 
('TICKETING_MANAGEMENT_MARKING', 'ticketing.adminFeature.ManageTicketingMarking.name', 2, 'jsp/admin/plugins/ticketing/admin/ManageMarkings.jsp', 'ticketing.adminFeature.ManageTicketingMarking.description', 0, 'ticketing', 'APPLICATIONS', NULL, NULL, @id_order_max, 0);
INSERT INTO core_user_right VALUES ('TICKETING_MANAGEMENT_MARKING', 1);
