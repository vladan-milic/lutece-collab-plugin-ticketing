DELETE FROM genatt_entry_type WHERE id_type >= 200 AND id_type <= 300;

INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(201,'Bouton radio',0,0,0,'ticketing.entryTypeRadioButton','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(202,'Case à cocher',0,0,0,'ticketing.entryTypeCheckBox','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(203,'Commentaire',0,1,0,'ticketing.entryTypeComment','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(204,'Date',0,0,0,'ticketing.entryTypeDate','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(205,'Liste déroulante',0,0,0,'ticketing.entryTypeSelect','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(206,'Zone de texte court',0,0,0,'ticketing.entryTypeText','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(207,'Zone de texte long',0,0,0,'ticketing.entryTypeTextArea','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(208,'Numérotation',0,0,0,'ticketing.entryTypeNumbering','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(209,'Regroupement',1,0,0,'ticketing.entryTypeGroup','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(210,'Liste déroulante SQL',0,0,0,'ticketing.entryTypeSelectSQL','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(211,'Géolocalisation',0,0,0,'ticketing.entryTypeGeolocation','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(212,'Session',0,0,0,'ticketing.entryTypeSession','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(213,'Utilisateur MyLutece',0,0,1,'ticketing.entryTypeMyLuteceUser','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(214,'Image',0,0,0,'ticketing.entryTypeImage','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(215,'Fichier',0,0,0,'ticketing.entryTypeFile','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(216,'Numéro de téléphone',0,0,0,'ticketing.entryTypePhone','ticketing');

DELETE FROM genatt_entry WHERE id_entry > 200 AND id_entry < 300 ;  
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique, map_provider, css_class, pos_conditional, error_message, num_row, num_column, is_role_associated, code) VALUES
(201, 1, "TICKET_FORM", 215, NULL, "Relevé d'imposition sur le revenu de l'année N-2", "Veuillez transmettre votre relevé d'imposition sur le revenu de l'année N-2. Si vous ne disposez pas de ce document, vous pourrez le transmettre à nos services ultérieurement.", "", 0, 0, 2, NULL, 0, NULL, 0, "", "", 0, NULL, 0, 0, 0, "IncomeRevenue-Year-2"), 
(202, 2, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber"), 
(203, 3, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber"), 
(204, 1, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber");

DELETE FROM genatt_field WHERE id_entry > 200 AND id_entry < 300 ;
INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title, comment, role_key, code) VALUES
(201, 201, NULL, NULL, 0, 255, 0, 0, 1, NULL, 0, NULL, NULL, NULL), 
(202, 201, "max_files", "1", 0, 0, 0, 0, 2, NULL, 0, NULL, NULL, NULL), 
(203, 201, "file_max_size", "1000000", 0, 0, 0, 0, 3, NULL, 0, NULL, NULL, NULL), 
(204, 201, "export_binary", "false", 0, 0, 0, 0, 4, NULL, 0, NULL, NULL, NULL), 
(205, 202, NULL, "", 0, 50, 0, -1, 5, NULL, 0, NULL, NULL, "FFAccountNumber"), 
(206, 203, NULL, "", 0, 50, 0, -1, 6, NULL, 0, NULL, NULL, "FFAccountNumber"), 
(207, 204, NULL, "", 0, 50, 0, -1, 7, NULL, 0, NULL, NULL, "FFAccountNumber");
