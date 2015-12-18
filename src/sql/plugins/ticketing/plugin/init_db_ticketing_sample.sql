INSERT INTO ticketing_ticket_category (id_ticket_category, id_ticket_domain, label) VALUES
(0, 1, "Autre"),
(1, 1, "Problème tarifaire petite enfance"),
(2, 1, "Problème tarifaire périscolaire");

INSERT INTO ticketing_ticket_domain (id_ticket_domain, id_ticket_type, label) VALUES
(0, 2, "Autre"),
(1, 2, "Facil'familles");

INSERT INTO ticketing_ticket_type (id_ticket_type, label) VALUES
(1, "Demande d'informations"),
(2, "Réclamations");

INSERT INTO ticketing_user_title (id_user_title, label) VALUES
(1, "M."),
(2, "Mme");

INSERT INTO ticketing_contact_mode (id_contact_mode, label, confirmation_msg) VALUES
(1, "E-mail", ""),
(2, "Téléphone fixe", ""),
(3, "Téléphone portable", "");
