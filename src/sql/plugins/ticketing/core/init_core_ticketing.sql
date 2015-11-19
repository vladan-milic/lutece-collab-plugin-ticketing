
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_MANAGEMENT','ticketing.adminFeature.ManageAdminTicketing.name',1,'jsp/admin/plugins/ticketing/ManageTicketCategorys.jsp','ticketing.adminFeature.ManageAdminTicketing.description',0,'ticketing',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'TICKETING_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_MANAGEMENT',1);


--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_TICKETS_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_TICKETS_MANAGEMENT','ticketing.adminFeature.ManageTickets.name',1,'jsp/admin/plugins/ticketing/ManageTickets.jsp','ticketing.adminFeature.ManageTickets.description',0,'ticketing',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'TICKETING_TICKETS_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_TICKETS_MANAGEMENT',1);

