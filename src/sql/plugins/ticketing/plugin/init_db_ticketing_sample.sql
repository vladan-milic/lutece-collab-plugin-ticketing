INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique, map_provider, css_class, pos_conditional, error_message, num_row, num_column, is_role_associated, code) VALUES
(1, 1, "TICKET_FORM", 215, NULL, "Relevé d'imposition sur le revenu de l'année N-2", "Veuillez-uploader votre relevé d'imposition sur le revenu de l'année N-2", "", 0, 0, 2, NULL, 0, NULL, 0, "", "", 0, NULL, 0, 0, 0, "IncomeRevenue-Year-2"), 
(2, 2, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "Numéro de compte Facil'familles", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber"), 
(3, 3, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "Numéro de compte Facil'familles", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber"), 
(4, 1, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "Numéro de compte Facil'familles", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber");


INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title, comment, role_key, code) VALUES
(1, 1, NULL, NULL, 0, 255, 0, 0, 1, NULL, 0, NULL, NULL, NULL), 
(2, 1, "max_files", "1", 0, 0, 0, 0, 2, NULL, 0, NULL, NULL, NULL), 
(3, 1, "file_max_size", "1000000", 0, 0, 0, 0, 3, NULL, 0, NULL, NULL, NULL), 
(4, 1, "export_binary", "false", 0, 0, 0, 0, 4, NULL, 0, NULL, NULL, NULL), 
(5, 2, NULL, "", 0, 50, 0, -1, 5, NULL, 0, NULL, NULL, "FFAccountNumber"), 
(6, 3, NULL, "", 0, 50, 0, -1, 6, NULL, 0, NULL, NULL, "FFAccountNumber"), 
(7, 4, NULL, "", 0, 50, 0, -1, 7, NULL, 0, NULL, NULL, "FFAccountNumber");

INSERT INTO ticketing_ticket_form (id_form, title, description) VALUES
(1, "Facil'Familles - Formulaire problème tarifaire petite enfance", "Formulaire du domaine Facil'Familles - Formulaire de la problématique 'problème tarifaire petite enfance'"), 
(2, "Facil'Familles - Problèmes tarifaires périscolaire", "Formulaire du domaine Facil'Familles - Formulaire de la problématique  'Problèmes tarifaires périscolaire'"), 
(3, "Facil'Familles - Autre", "Formulaire du domaine Facil'Familles - problématique 'Autre'");


INSERT INTO ticketing_ticket_type (id_ticket_type, label) VALUES
(1, "Demande d'information"),
(2, "Réclamation");

INSERT INTO ticketing_ticket_domain (id_ticket_domain, id_ticket_type, label) VALUES
(100, 1, "Autre" ),
(110, 1, "Mairie" ),
(120, 1, "Stationnement" ),
(200, 2, "Autre" ),
(210, 2, "Facil'familles" );

INSERT INTO ticketing_ticket_category (id_ticket_category, id_ticket_domain, label, category_code, id_ticket_form ) VALUES
(1, 100, "Autre" , NULL, 0 ),
(2, 110, "Réservation de salle" , NULL, 0 ),
(3, 110, "Autre" , NULL, 0 ),
(4, 120, "Horaires de stationnement" , "DVDSSVP", 0 ),
(5, 120, "Autre" , NULL, 0 ),
(6, 200, "Autre" , NULL, 0 ),
(7, 210, "Problème tarifaire périscolaire" , "FFTARIFPERISCO", 2 ),
(8, 210, "Problème tarifaire petite enfance" , "FFTARIFPE", 1 ),
(9, 210, "Autre" , NULL, 3 );


INSERT INTO ticketing_user_title (id_user_title, label) VALUES
(1, "M."),
(2, "Mme");

INSERT INTO ticketing_contact_mode (id_contact_mode, label, confirmation_msg) VALUES
(1, "E-mail", ""),
(2, "Téléphone fixe", ""),
(3, "Téléphone portable", "");
