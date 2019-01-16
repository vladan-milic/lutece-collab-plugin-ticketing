INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique, map_provider, css_class, pos_conditional, error_message, num_row, num_column, is_role_associated, code) VALUES
(100000, 1, "REPLY_ATTACHMENTS", 215, NULL, "Pièces(s) jointe(s)", "Vous pouvez sélectionner des pièces jointes à ajouter à votre message", "", 0, 0, 1, NULL, 0, NULL, 0, "", "", 0, NULL, 0, 0, 0, "attached_files");

INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title, comment, role_key, code) VALUES
(100000, 100000, NULL, NULL, 0, 255, 0, 0, 1, NULL, 0, NULL, NULL, NULL),
(100001, 100000, "max_files", "5", 0, 0, 0, 0, 2, NULL, 0, NULL, NULL, NULL),
(100002, 100000, "file_max_size", "1000000", 0, 0, 0, 0, 3, NULL, 0, NULL, NULL, NULL),
(100003, 100000, "export_binary", "false", 0, 0, 0, 0, 4, NULL, 0, NULL, NULL, NULL);