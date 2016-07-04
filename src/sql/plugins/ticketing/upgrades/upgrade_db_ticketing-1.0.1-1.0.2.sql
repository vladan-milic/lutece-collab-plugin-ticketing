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

--
--  Remove nullable constraint on phone numbers
--
ALTER TABLE ticketing_ticket MODIFY fixed_phone_number varchar(50) NULL default '';
ALTER TABLE ticketing_ticket MODIFY mobile_phone_number varchar(50) NULL default '';

--
--  Remove default value
--
ALTER TABLE ticketing_ticket MODIFY id_user_title int(11) NOT NULL;
ALTER TABLE ticketing_ticket MODIFY firstname varchar(50) NOT NULL;
ALTER TABLE ticketing_ticket MODIFY lastname varchar(50) NOT NULL;
ALTER TABLE ticketing_ticket MODIFY email varchar(255) NOT NULL;
ALTER TABLE ticketing_ticket MODIFY id_ticket_category int(11) NOT NULL;
ALTER TABLE ticketing_ticket MODIFY id_contact_mode int(11) NOT NULL;
ALTER TABLE ticketing_ticket MODIFY id_channel int(11) NULL;
ALTER TABLE ticketing_instant_response MODIFY id_ticket_category int(11) NOT NULL;
ALTER TABLE ticketing_instant_response MODIFY id_channel int(11) NOT NULL;
ALTER TABLE ticketing_ticket_category MODIFY id_ticket_domain int(11) NOT NULL;
ALTER TABLE ticketing_ticket_domain MODIFY id_ticket_type int(11) NOT NULL;

--
--  Change default value
--
ALTER TABLE ticketing_ticket MODIFY date_create timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE ticketing_instant_response MODIFY date_create timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;

--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_PLUGIN_CONFIGURATION';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_PLUGIN_CONFIGURATION','ticketing.adminFeature.ConfigurePlugin.name',1,'jsp/admin/plugins/ticketing/config/ConfigurePlugin.jsp','ticketing.adminFeature.ConfigurePlugin.description',0,'ticketing',NULL,NULL,NULL,4);

--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'TICKETING_PLUGIN_CONFIGURATION';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_PLUGIN_CONFIGURATION',1);

--
-- Structure for table ticketing_model_reponses
--

DROP TABLE IF EXISTS ticketing_model_reponses;
CREATE TABLE `ticketing_model_reponses` (
  `id_model_response` int(6) NOT NULL,
  `id_ticket_category` int(11) NOT NULL DEFAULT '0',
  `title` varchar(500) NOT NULL DEFAULT '',
  `reponse` text,
  `keyword` text
);


ALTER TABLE `ticketing_model_reponses` ADD PRIMARY KEY (`id_model_response`);

ALTER TABLE ticketing_model_reponses ADD CONSTRAINT fk_ticketing_model_reponses_category FOREIGN KEY (id_ticket_category)
      REFERENCES ticketing_ticket_category (id_ticket_category) ON DELETE RESTRICT ON UPDATE RESTRICT;    

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT_MODEL_RESPONSE';
INSERT INTO core_admin_right (`id_right`, `name`, `level_right`, `admin_url`, `description`, `is_updatable`, `plugin_name`, `id_feature_group`, `icon_url`, `documentation_url`, `id_order`) VALUES
('TICKETING_MANAGEMENT_MODEL_RESPONSE', 'ticketing.adminFeature.ManageTicketingModelResponse.name', '2', 'jsp/admin/plugins/ticketing/admin/ManageModelResponses.jsp', 'ticketing.adminFeature.ManageTicketingModelResponse.description', '0', 'ticketing', "GRU_ADMIN", NULL, NULL, '23');

DELETE FROM core_user_right WHERE id_right = 'TICKETING_MANAGEMENT_MODEL_RESPONSE';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_MANAGEMENT_MODEL_RESPONSE',1);

ALTER TABLE ticketing_contact_mode CHANGE COLUMN label code varchar(50) NOT NULL default '';


DELETE FROM core_datastore WHERE core_datastore.entity_key = "core.daemon.modelResponsesIndexerDaemon.interval";
DELETE FROM core_datastore WHERE core_datastore.entity_key = "core.daemon.modelResponsesIndexerDaemon.onStartUp";
INSERT INTO core_datastore (`entity_key`, `entity_value`) VALUES
('core.daemon.modelResponsesIndexerDaemon.interval', '6000'),
('core.daemon.modelResponsesIndexerDaemon.onStartUp', 'true');