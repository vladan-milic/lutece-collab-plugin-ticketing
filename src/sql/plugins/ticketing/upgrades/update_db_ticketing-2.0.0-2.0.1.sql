
--
-- Structure for table ticketing_form
--

DROP TABLE IF EXISTS ticketing_form;
DROP TABLE IF EXISTS ticketing_form;
CREATE TABLE ticketing_form (
id_form int(6) NOT NULL,
title long varchar,
message long varchar,
button_label long varchar,
connection SMALLINT,
PRIMARY KEY (id_form)
);


--
-- Structure for table ticketing_formentry
--

DROP TABLE IF EXISTS ticketing_formentry;
CREATE TABLE ticketing_formentry (
id_formentry int(6) NOT NULL,
id_form int(11) default '0' NOT NULL,
id_champ varchar(50) default '' NOT NULL,
hidden SMALLINT default '0' NOT NULL,
mandatory SMALLINT default '0' NOT NULL,
hierarchy int(11) default '0' NOT NULL,
default_value varchar(500) default '',
PRIMARY KEY (id_formentry)
);


--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_TICKETS_MANAGEMENT_FORMS';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_TICKETS_MANAGEMENT_FORMS','ticketing.adminFeature.ManageAdminTicketingForms.name',1,'jsp/admin/plugins/ticketing/admin/ManageForms.jsp','ticketing.adminFeature.ManageAdminTicketingForms.description',0,'ticketing','APPLICATIONS',NULL,NULL,7);

--
-- Update authentication entries
--
UPDATE core_datastore SET entity_value='false' WHERE entity_key='mylutece.portal.authentication.required';
DELETE FROM core_datastore WHERE entity_key = 'mylutece.security.public_url.ticketing';
INSERT INTO core_datastore (entity_key, entity_value) VALUES ('mylutece.security.public_url.confirmticket', 'jsp/site/Portal.jsp?page=ticket&view=confirmTicket');
