-- Delete old constraints
ALTER TABLE ticketing_ticket_domain DROP FOREIGN KEY fk_ticketing_ticket_domain_type; 
ALTER TABLE ticketing_ticket_category DROP FOREIGN KEY fk_ticketing_ticket_category_domain; 
ALTER TABLE ticketing_ticket DROP FOREIGN KEY fk_ticketing_ticket_category; 
ALTER TABLE ticketing_instant_response DROP FOREIGN KEY fk_ticketing_instant_response_category; 
-- Delete old tables
DROP TABLE IF EXISTS ticketing_ticket_category; 
DROP TABLE IF EXISTS ticketing_ticket_domain; 
DROP TABLE IF EXISTS ticketing_ticket_type; 
DROP TABLE IF EXISTS ticketing_ticket_category_input; 

--
-- Structure for table ticketing_category
--
DROP TABLE IF EXISTS ticketing_category;
CREATE TABLE ticketing_category (
id_category int(6) NOT NULL,
id_parent int(6) NOT NULL,
label varchar(255) NOT NULL default '',
n_order int(6) DEFAULT 0 NOT NULL,
code varchar(50) NULL,
id_default_assignee_unit int(6) NOT NULL default '0',
id_category_type int(6) NOT NULL default '0',
id_workflow int(6) NOT NULL default '0',
demand_id int(6) NOT NULL default '0',
help_message varchar(500) NULL,
is_manageable int(1) DEFAULT 0 NOT NULL,
inactive int(1)  NOT NULL default '0',
PRIMARY KEY (id_category)
);

--
-- Structure for table ticketing_category_type
--
DROP TABLE IF EXISTS ticketing_category_type;
CREATE TABLE ticketing_category_type (
id_category_type int(6) NOT NULL,
label varchar(255) NOT NULL default '',
depth int(6) DEFAULT 0 NOT NULL,
PRIMARY KEY (id_category_type)
);

--
-- Structure for table ticketing_category_input
--
DROP TABLE IF EXISTS ticketing_category_input;
CREATE TABLE ticketing_category_input (
    id_category int(6) NOT NULL,
    id_input int(6) NOT NULL,
    pos int(6) NOT NULL default '0',
    PRIMARY KEY (id_category, id_input)
);

--
-- Update constraints
--
ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_category FOREIGN KEY (id_ticket_category)
      REFERENCES ticketing_category (id_category) ON DELETE RESTRICT ON UPDATE RESTRICT; 
      
ALTER TABLE ticketing_instant_response ADD CONSTRAINT fk_ticketing_instant_response_category FOREIGN KEY (id_ticket_category)
      REFERENCES ticketing_category (id_category) ON DELETE RESTRICT ON UPDATE RESTRICT; 

--
-- Update rights 
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES  
('TICKETING_MANAGEMENT','ticketing.adminFeature.ManageAdminTicketing.name',1,'jsp/admin/plugins/ticketing/admin/ManageCategories.jsp','ticketing.adminFeature.ManageAdminTicketing.description',0,'ticketing','APPLICATIONS',NULL,NULL,1); 

--
-- Update generic attributes
--
DELETE FROM genatt_entry WHERE id_entry = 201;
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique, map_provider, css_class, pos_conditional, error_message, num_row, num_column, is_role_associated, code) VALUES 
(201, 1, "TICKET_INPUT", 215, NULL, "Relevé d'imposition sur le revenu de l'année N-2", "Veuillez transmettre votre relevé d'imposition sur le revenu de l'année N-2. Si vous ne disposez pas de ce document, vous pourrez le transmettre à nos services ultérieurement.", "", 0, 0, 1, NULL, 0, NULL, 0, "", "", 0, NULL, 0, 0, 0, "IncomeRevenue-Year-2"),  
(202, 2, "TICKET_INPUT", 206, NULL, "Numéro de compte facil'familles", "Numéro de compte facil'familles", "", 0, 0, 2, NULL, 0, NULL, 0, "", "", 0, NULL, 0, 0, 0, "FFAccountNumber");  
