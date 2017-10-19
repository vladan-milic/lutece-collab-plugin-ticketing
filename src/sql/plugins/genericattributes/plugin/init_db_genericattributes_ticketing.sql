DELETE FROM genatt_field WHERE id_entry > 200 AND id_entry < 300 ;
DELETE FROM genatt_entry WHERE id_entry > 200 AND id_entry < 300 ;
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
(214,'Image',0,0,0,'ticketing.entryTypeImage','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES
(215,'Fichier',0,0,0,'ticketing.entryTypeFile','ticketing');
INSERT INTO genatt_entry_type (id_type,title,is_group,is_comment,is_mylutece_user,class_name,plugin) VALUES 
(216,'Numéro de téléphone',0,0,0,'ticketing.entryTypePhone','ticketing');
  
INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique, map_provider, css_class, pos_conditional, error_message, num_row, num_column, is_role_associated, code) VALUES
(201, 1, "TICKET_INPUT", 215, NULL, "Relevé d'imposition sur le revenu de l'année N-2", "Veuillez transmettre votre relevé d'imposition sur le revenu de l'année N-2. Si vous ne disposez pas de ce document, vous pourrez le transmettre à nos services ultérieurement.", "", 0, 0, 1, NULL, 0, NULL, 0, "", "", 0, NULL, 0, 0, 0, "IncomeRevenue-Year-2"), 
(202, 2, "TICKET_INPUT", 206, NULL, "Numéro de compte facil'familles", "Numéro de compte facil'familles", "", 0, 0, 2, NULL, 0, NULL, 0, "", "", 0, NULL, 0, 0, 0, "FFAccountNumber"); 

INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title, comment, role_key, code) VALUES
(201, 201, NULL, NULL, 0, 255, 0, 0, 1, NULL, 0, NULL, NULL, NULL), 
(202, 201, "max_files", "1", 0, 0, 0, 0, 2, NULL, 0, NULL, NULL, NULL), 
(203, 201, "file_max_size", "1000000", 0, 0, 0, 0, 3, NULL, 0, NULL, NULL, NULL), 
(204, 201, "export_binary", "false", 0, 0, 0, 0, 4, NULL, 0, NULL, NULL, NULL);
