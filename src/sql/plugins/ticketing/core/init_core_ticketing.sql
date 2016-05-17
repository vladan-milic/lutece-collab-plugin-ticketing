
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
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_MANAGEMENT','ticketing.adminFeature.ManageAdminTicketing.name',1,'jsp/admin/plugins/ticketing/admin/ManageTicketCategorys.jsp','ticketing.adminFeature.ManageAdminTicketing.description',0,'ticketing',NULL,NULL,NULL,4);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_FORM_MANAGEMENT';          
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_FORM_MANAGEMENT','ticketing.adminFeature.ManageTicketForms.name',1,'jsp/admin/plugins/ticketing/admin/ManageTicketForms.jsp','ticketing.adminFeature.ManageTicketForms.description',0,'ticketing',NULL,NULL,NULL,5);

--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'TICKETING_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_FORM_MANAGEMENT',1);


--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_TICKETS_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_TICKETS_MANAGEMENT','ticketing.adminFeature.ManageTickets.name',1,'jsp/admin/plugins/ticketing/ManageTickets.jsp','ticketing.adminFeature.ManageTickets.description',0,'ticketing',NULL,NULL,NULL,4);

DELETE FROM core_admin_right WHERE id_right = 'TICKETING_USER_PREFERENCES_MANAGEMENT';  
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_USER_PREFERENCES_MANAGEMENT','ticketing.adminFeature.ManageUserPreferences.name',1,'jsp/admin/plugins/ticketing/UserPreferences.jsp','ticketing.adminFeature.ManageUserPreferences.description',0,'ticketing',NULL,NULL,NULL,6);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'TICKETING_TICKETS_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_TICKETS_MANAGEMENT',1);
DELETE FROM core_user_right WHERE id_right = 'TICKETING_USER_PREFERENCES_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_USER_PREFERENCES_MANAGEMENT',1);

--
-- Init  table core_dashboard
--
INSERT INTO core_dashboard(dashboard_name, dashboard_column, dashboard_order) VALUES('TICKETING', 1, 5);


--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_INSTANT_RESPONSE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_INSTANT_RESPONSE_MANAGEMENT','ticketing.adminFeature.ManageInstantResponse.name',1,'jsp/admin/plugins/ticketing/ManageInstantResponses.jsp','ticketing.adminFeature.ManageInstantResponse.description',0,'ticketing',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'TICKETING_INSTANT_RESPONSE_MANAGEMENT' AND id_user = 1;
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_INSTANT_RESPONSE_MANAGEMENT',1);

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



