INSERT INTO genatt_entry (id_entry, id_resource, resource_type, id_type, id_parent, title, help_message, comment, mandatory, fields_in_line, pos, id_field_depend, confirm_field, confirm_field_title, field_unique, map_provider, css_class, pos_conditional, error_message, num_row, num_column, is_role_associated, code) VALUES
(201, 1, "TICKET_FORM", 215, NULL, "Relevé d'imposition sur le revenu de l'année N-2", "Veuillez transmettre votre relevé d'imposition sur le revenu de l'année N-2. Si vous ne disposez pas de ce document, vous pourrez le transmettre à nos services ultérieurement.", "", 0, 0, 2, NULL, 0, NULL, 0, "", "", 0, NULL, 0, 0, 0, "IncomeRevenue-Year-2"), 
(202, 2, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "Numéro de compte Facil'familles", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber"), 
(203, 3, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "Numéro de compte Facil'familles", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber"), 
(204, 1, "TICKET_FORM", 206, NULL, "Numéro de compte Facil'familles", "Numéro de compte Facil'familles", "", 1, 0, 1, NULL, 0, NULL, 0, "", "", 0, "Le numéro de compte Facil'familles est un champ obligatoire, veuillez le renseigner.", 0, 0, 0, "FFAccountNumber");


INSERT INTO genatt_field (id_field, id_entry, title, value, height, width, default_value, max_size_enter, pos, value_type_date, no_display_title, comment, role_key, code) VALUES
(201, 201, NULL, NULL, 0, 255, 0, 0, 1, NULL, 0, NULL, NULL, NULL), 
(202, 201, "max_files", "1", 0, 0, 0, 0, 2, NULL, 0, NULL, NULL, NULL), 
(203, 201, "file_max_size", "1000000", 0, 0, 0, 0, 3, NULL, 0, NULL, NULL, NULL), 
(204, 201, "export_binary", "false", 0, 0, 0, 0, 4, NULL, 0, NULL, NULL, NULL), 
(205, 202, NULL, "", 0, 50, 0, -1, 5, NULL, 0, NULL, NULL, "FFAccountNumber"), 
(206, 203, NULL, "", 0, 50, 0, -1, 6, NULL, 0, NULL, NULL, "FFAccountNumber"), 
(207, 204, NULL, "", 0, 50, 0, -1, 7, NULL, 0, NULL, NULL, "FFAccountNumber");

INSERT INTO ticketing_ticket_form (id_form, title, description) VALUES
(1, "Facil'Familles - Formulaire problème tarifaire petite enfance", "Formulaire du domaine Facil'Familles - Formulaire de la problématique 'problème tarifaire petite enfance'"), 
(2, "Facil'Familles - Problèmes tarifaires périscolaire", "Formulaire du domaine Facil'Familles - Formulaire de la problématique  'Problèmes tarifaires périscolaire'"), 
(3, "Facil'Familles - Autre", "Formulaire du domaine Facil'Familles - problématique 'Autre'");


INSERT INTO ticketing_ticket_type (id_ticket_type, label, reference_prefix) VALUES
(1, "Demande d'information", "INF"),
(2, "Réclamation", "RCL");

INSERT INTO ticketing_ticket_domain (id_ticket_domain, id_ticket_type, label) VALUES
(100, 1, "Autre" ),
(110, 1, "Mairie" ),
(120, 1, "Stationnement" ),
(200, 2, "Autre" ),
(210, 2, "Facil'familles" );

INSERT INTO ticketing_ticket_category (id_ticket_category, id_ticket_domain, label, category_code, id_ticket_form, id_workflow ) VALUES
(1, 100, "Autre" , NULL, 0, 301 ),
(2, 110, "Réservation de salle" , NULL, 0, 301 ),
(3, 110, "Autre" , NULL, 0, 301 ),
(4, 120, "Horaires de stationnement" , "DVDSSVP", 0, 301 ),
(5, 120, "Autre" , NULL, 0, 301 ),
(6, 200, "Autre" , NULL, 0, 301 ),
(7, 210, "Problème tarifaire périscolaire" , "FFTARIFPERISCO", 2, 301 ),
(8, 210, "Problème tarifaire petite enfance" , "FFTARIFPE", 1, 301 ),
(9, 210, "Autre" , NULL, 3, 301 );


INSERT INTO ticketing_user_title (id_user_title, label) VALUES
(1, "M."),
(2, "Mme");

INSERT INTO ticketing_contact_mode (id_contact_mode, label, confirmation_msg) VALUES
(1, "E-mail", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un e-mail de confirmation vous a &eacute;t&eacute; envoy&eacute; &agrave; l'adresse suivante&nbsp;${email}. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
 <p>Nous restons &agrave; votre enti&egrave;re disposition pour toute information compl&eacute;mentaire.</p>
 <p>Cordialement,</p>
 <p>Mairie de Paris.</p>"),
(2, "Téléphone fixe", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un e-mail de confirmation vous a &eacute;t&eacute; envoy&eacute; &agrave; l'adresse suivante&nbsp;${email}. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
 <p>Nous restons &agrave; votre enti&egrave;re disposition pour toute information compl&eacute;mentaire.</p>
 <p>Cordialement,</p>
 <p>Mairie de Paris.</p>"),
(3, "Téléphone portable", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un sms de confirmation vous a &eacute;t&eacute; envoy&eacute; au num&eacute;ro suivant&nbsp;${mobilePhoneNumber}. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
 <p>Nous restons &agrave; votre enti&egrave;re disposition pour toute information compl&eacute;mentaire.</p>
 <p>Cordialement,</p>
 <p>Mairie de Paris.</p>");
