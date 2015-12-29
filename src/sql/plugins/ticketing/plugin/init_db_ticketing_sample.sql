
INSERT INTO ticketing_ticket_type (id_ticket_type, label) VALUES
(1, "Demande d'informations"),
(2, "Réclamations");

INSERT INTO ticketing_ticket_domain (id_ticket_domain, id_ticket_type, label) VALUES
(1, 1, "Autre"),
(10, 2, "Facil'familles"),
(20, 2, "Stationnement"),
(100, 2, "Autre");

INSERT INTO ticketing_ticket_category (id_ticket_category, id_ticket_domain, label, category_code ) VALUES
(1, 1, "Autre" , NULL ),
(2, 10, "Problème tarifaire petite enfance" , "FFTARIFPE" ),
(3, 10, "Problème tarifaire périscolaire" , "FFTARIFPERISCO" ),
(4, 10, "Autre" , NULL ),
(5, 20, "Carte de stationnement" , "DVDSSVP" ),
(6, 20, "Autre" , NULL ),
(7, 100, "Autre" , NULL );


INSERT INTO ticketing_user_title (id_user_title, label) VALUES
(1, "M."),
(2, "Mme");

INSERT INTO ticketing_contact_mode (id_contact_mode, label, confirmation_msg) VALUES
(1, "E-mail", ""),
(2, "Téléphone fixe", ""),
(3, "Téléphone portable", "");
