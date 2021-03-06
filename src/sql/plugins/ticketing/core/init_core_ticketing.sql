
--
-- Data for table core_admin_user
--
DELETE FROM core_admin_user WHERE access_code = 'ticketing_userfront';
INSERT INTO core_admin_user VALUES 
(5,'ticketing_userfront','Usager de la Mairie de Paris',' ','ticketing_userfront@lutece.fr',0,'ticketing','fr',0,0,0,null,null,0,'1980-01-01 00:00:00','all')
;

--
-- Data for table core_admin_role
--
DELETE FROM core_admin_role WHERE role_key LIKE 'ticketing_%';
INSERT INTO core_admin_role ( role_key , role_description ) VALUES 
( 'ticketing_user_front' ,'Droit pour le user de front de ticketing' )
;

DELETE FROM core_user_role WHERE role_key LIKE 'ticketing_%';
INSERT INTO core_user_role ( role_key , id_user ) VALUES
( 'ticketing_user_front' , 5 )
;

--
-- Data for table core_feature_group
--
UPDATE core_feature_group SET feature_group_order = feature_group_order + 1 WHERE id_feature_group <> "TICKETING";
DELETE FROM core_feature_group WHERE id_feature_group = "TICKETING"; 
INSERT INTO core_feature_group (id_feature_group, feature_group_description, feature_group_label, feature_group_order) VALUES ('TICKETING','ticketing.adminFeature.group.ticketing.description','ticketing.adminFeature.group.ticketing.label',1);

--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_TICKETS_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_TICKETS_MANAGEMENT','ticketing.adminFeature.ManageTickets.name',3,'jsp/admin/plugins/ticketing/ManageTickets.jsp','ticketing.adminFeature.ManageTickets.description',0,'ticketing','TICKETING',NULL,NULL,1);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_INSTANT_RESPONSE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_INSTANT_RESPONSE_MANAGEMENT','ticketing.adminFeature.ManageInstantResponse.name',1,'jsp/admin/plugins/ticketing/ManageInstantResponses.jsp','ticketing.adminFeature.ManageInstantResponse.description',0,'ticketing','TICKETING',NULL,NULL,2);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_USER_PREFERENCES_MANAGEMENT';  
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_USER_PREFERENCES_MANAGEMENT','ticketing.adminFeature.ManageUserPreferences.name',3,'jsp/admin/plugins/ticketing/UserPreferences.jsp','ticketing.adminFeature.ManageUserPreferences.description',0,'ticketing','TICKETING',NULL,NULL,3);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT_MARKING';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_MANAGEMENT_MARKING', 'ticketing.adminFeature.ManageTicketingMarking.name', 2, 'jsp/admin/plugins/ticketing/admin/ManageMarkings.jsp', 'ticketing.adminFeature.ManageTicketingMarking.description', 0, 'ticketing', 'APPLICATIONS', NULL, NULL, 4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'TICKETING_TICKETS_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_TICKETS_MANAGEMENT',1);
DELETE FROM core_user_right WHERE id_right = 'TICKETING_INSTANT_RESPONSE_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_INSTANT_RESPONSE_MANAGEMENT',1);
DELETE FROM core_user_right WHERE id_right = 'TICKETING_USER_PREFERENCES_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_USER_PREFERENCES_MANAGEMENT',1);
DELETE FROM core_user_right WHERE id_right = 'TICKETING_MANAGEMENT_MARKING';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_MANAGEMENT_MARKING', 1);

--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_MANAGEMENT','ticketing.adminFeature.ManageAdminTicketing.name',1,'jsp/admin/plugins/ticketing/admin/ManageCategories.jsp','ticketing.adminFeature.ManageAdminTicketing.description',0,'ticketing','APPLICATIONS',NULL,NULL,1);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_INPUT_MANAGEMENT';          
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_INPUT_MANAGEMENT','ticketing.adminFeature.ManageTicketInputs.name',1,'jsp/admin/plugins/ticketing/admin/ManageTicketInputs.jsp','ticketing.adminFeature.ManageTicketInputs.description',0,'ticketing','APPLICATIONS',NULL,NULL,2);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT_MODEL_RESPONSE';
INSERT INTO core_admin_right (`id_right`, `name`, `level_right`, `admin_url`, `description`, `is_updatable`, `plugin_name`, `id_feature_group`, `icon_url`, `documentation_url`, `id_order`) VALUES
('TICKETING_MANAGEMENT_MODEL_RESPONSE', 'ticketing.adminFeature.ManageTicketingModelResponse.name', '2', 'jsp/admin/plugins/ticketing/admin/ManageModelResponses.jsp', 'ticketing.adminFeature.ManageTicketingModelResponse.description', '0', 'ticketing', 'APPLICATIONS', NULL, NULL, '5');

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT_MARKING';
INSERT INTO core_admin_right (`id_right`, `name`, `level_right`, `admin_url`, `description`, `is_updatable`, `plugin_name`, `id_feature_group`, `icon_url`, `documentation_url`, `id_order`) VALUES
('TICKETING_MANAGEMENT_MARKING', 'ticketing.adminFeature.ManageTicketingMarking.name', '2', 'jsp/admin/plugins/ticketing/admin/ManageMarkings.jsp', 'ticketing.adminFeature.ManageTicketingMarking.description', '0', 'ticketing', 'APPLICATIONS', NULL, NULL, '4');

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_PLUGIN_CONFIGURATION';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_PLUGIN_CONFIGURATION','ticketing.adminFeature.ConfigurePlugin.name',1,'jsp/admin/plugins/ticketing/config/ConfigurePlugin.jsp','ticketing.adminFeature.ConfigurePlugin.description',0,'ticketing','APPLICATIONS',NULL,NULL,4);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_TICKETS_MANAGEMENT_FORMS';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_TICKETS_MANAGEMENT_FORMS','ticketing.adminFeature.ManageAdminTicketingForms.name',1,'jsp/admin/plugins/ticketing/admin/ManageForms.jsp','ticketing.adminFeature.ManageAdminTicketingForms.description',0,'ticketing','APPLICATIONS',NULL,NULL,7);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'TICKETING_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_MANAGEMENT',1);
DELETE FROM core_user_right WHERE id_right = 'TICKETING_INPUT_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_INPUT_MANAGEMENT',1);
DELETE FROM core_user_right WHERE id_right = 'TICKETING_MANAGEMENT_MODEL_RESPONSE';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_MANAGEMENT_MODEL_RESPONSE',1);
DELETE FROM core_user_right WHERE id_right = 'TICKETING_PLUGIN_CONFIGURATION';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_PLUGIN_CONFIGURATION',1);

--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('TICKETING', 1, 5);

