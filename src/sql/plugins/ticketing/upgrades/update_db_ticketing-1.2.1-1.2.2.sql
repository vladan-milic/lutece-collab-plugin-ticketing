--
-- Structure for table ticketing_category
--

ALTER TABLE ticketing_ticket_category ADD category_precision VARCHAR(150) NULL DEFAULT NULL;
ALTER TABLE ticketing_ticket_category ADD help_message VARCHAR(500) NULL;
ALTER TABLE ticketing_ticket_category DROP COLUMN id_ticket_form;
--
-- Data for table core_admin_right
--

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_MANAGEMENT','ticketing.adminFeature.ManageAdminTicketing.name',1,'jsp/admin/plugins/ticketing/admin/ManageTicketCategories.jsp','ticketing.adminFeature.ManageAdminTicketing.description',0,'ticketing','TICKETING_ADMIN',NULL,NULL,1);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_INPUT_MANAGEMENT';          
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_INPUT_MANAGEMENT','ticketing.adminFeature.ManageTicketInputs.name',1,'jsp/admin/plugins/ticketing/admin/ManageTicketInputs.jsp','ticketing.adminFeature.ManageTicketInputs.description',0,'ticketing','TICKETING_ADMIN',NULL,NULL,4);

DELETE FROM core_user_right WHERE id_right = 'TICKETING_INPUT_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_INPUT_MANAGEMENT',1);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_FORM_MANAGEMENT';
DELETE FROM core_user_right WHERE id_right = 'TICKETING_FORM_MANAGEMENT' AND id_user = 1;
--
-- Structure for table ticketing_ticket_category_input
--

DROP TABLE IF EXISTS ticketing_ticket_category_input;

CREATE TABLE ticketing_ticket_category_input (
    id_ticket_category int(6) NOT NULL,
    id_input int(6) NOT NULL,
    pos int(1) NOT NULL default '0',
    PRIMARY KEY (id_ticket_category, id_input)
);


DROP TABLE IF EXISTS ticketing_ticket_form;

--
-- Delete unused entry types
--
DELETE gvb
      FROM genatt_verify_by gvb
      JOIN genatt_field gf ON gvb.id_field = gf.id_field
      JOIN genatt_entry ge ON ge.id_entry = gf.id_entry
     WHERE ge.id_type >= 208 AND ge.id_type <= 213;
     
DELETE FROM genatt_response WHERE id_entry IN ( 
   SELECT id_entry FROM genatt_entry WHERE id_type >= 208 AND id_type <= 213 );

DELETE FROM genatt_field WHERE id_entry IN ( 
   SELECT id_entry FROM genatt_entry WHERE id_type >= 208 AND id_type <= 213 );

DELETE FROM genatt_entry WHERE id_type >= 208 AND id_type <= 213;

DELETE FROM genatt_entry_type WHERE id_type >= 208 AND id_type <= 213;


