
INSERT INTO ticketing_ticket_type (id_ticket_type, label) VALUES
(1, "Demande d'information"),
(2, "Réclamation");

INSERT INTO ticketing_ticket_domain (id_ticket_domain, id_ticket_type, label) VALUES
(100, 1, "Autre" ),
(110, 1, "Mairie" ),
(120, 1, "Stationnement" ),
(200, 2, "Autre" ),
(210, 2, "Facil'familles" );

INSERT INTO ticketing_ticket_category (id_ticket_category, id_ticket_domain, label, category_code ) VALUES
(1, 100, "Autre" , NULL ),
(2, 110, "Réservation de salle" , NULL ),
(3, 110, "Autre" , NULL ),
(4, 120, "Horaires de stationnement" , "DVDSSVP" ),
(5, 120, "Autre" , NULL ),
(6, 200, "Autre" , NULL ),
(7, 210, "Problème tarifaire périscolaire" , "FFTARIFPERISCO" ),
(8, 210, "Problème tarifaire petite enfance" , "FFTARIFPE" ),
(9, 210, "Autre" , NULL );


INSERT INTO ticketing_user_title (id_user_title, label) VALUES
(1, "M."),
(2, "Mme");

INSERT INTO ticketing_contact_mode (id_contact_mode, label, confirmation_msg) VALUES
(1, "E-mail", ""),
(2, "Téléphone fixe", ""),
(3, "Téléphone portable", "");
