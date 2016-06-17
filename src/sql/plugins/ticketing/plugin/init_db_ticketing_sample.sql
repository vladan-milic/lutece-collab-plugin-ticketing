DELETE FROM ticketing_ticket_form ;
INSERT INTO ticketing_ticket_form (id_form, title, description) VALUES
(1, "Facil'Familles - Formulaire problème tarifaire petite enfance", "Formulaire du domaine Facil'Familles - Formulaire de la problématique 'problème tarifaire petite enfance'"), 
(2, "Facil'Familles - Problèmes tarifaires périscolaire", "Formulaire du domaine Facil'Familles - Formulaire de la problématique  'Problèmes tarifaires périscolaire'"), 
(3, "Facil'Familles - Autre", "Formulaire du domaine Facil'Familles - problématique 'Autre'");

DELETE FROM ticketing_ticket_category ;
DELETE FROM ticketing_ticket_domain ;
DELETE FROM ticketing_ticket_type ;
INSERT INTO ticketing_ticket_type (id_ticket_type, label, reference_prefix) VALUES
(1, "Demande d'information", "INF"),
(2, "Réclamation", "RCL");

INSERT INTO ticketing_ticket_domain (id_ticket_domain, id_ticket_type, label) VALUES
(100, 1, "Autre" ),
(110, 1, "Mairie" ),
(120, 1, "Stationnement" ),
(200, 2, "Autre" ),
(210, 2, "Facil'familles" );

INSERT INTO ticketing_ticket_category (id_ticket_category, id_ticket_domain, label, category_code, id_ticket_form, id_workflow, id_unit ) VALUES
(1, 100, "Autre" , NULL, 0, 301, 0 ),
(2, 110, "Réservation de salle" , NULL, 0, 301, 0 ),
(3, 110, "Autre" , NULL, 0, 301, 0 ),
(4, 120, "Horaires de stationnement" , "DVDSSVP", 0, 301, 0 ),
(5, 120, "Autre" , NULL, 0, 301, 0 ),
(6, 200, "Autre" , NULL, 0, 301, 0 ),
(7, 210, "Problème tarifaire périscolaire" , "FFTARIFPERISCO", 2, 301, 0 ),
(8, 210, "Problème tarifaire petite enfance" , "FFTARIFPE", 1, 301, 0 ),
(9, 210, "Autre" , NULL, 3, 301, 0 );

DELETE FROM ticketing_user_title ;
INSERT INTO ticketing_user_title (id_user_title, label) VALUES
(1, ""),
(2, "M."),
(3, "Mme");

DELETE FROM ticketing_contact_mode ;
INSERT INTO ticketing_contact_mode (id_contact_mode, code, confirmation_msg) VALUES
(1, "email", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un e-mail de confirmation vous a &eacute;t&eacute; envoy&eacute; &agrave; l'adresse suivante&nbsp;${email}. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
 <p>Nous restons &agrave; votre enti&egrave;re disposition pour toute information compl&eacute;mentaire.</p>
 <p>Cordialement,</p>
 <p>Mairie de Paris.</p>"),
(2, "fixed_phone_number", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un e-mail de confirmation vous a &eacute;t&eacute; envoy&eacute; &agrave; l'adresse suivante&nbsp;${email}. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
 <p>Nous restons &agrave; votre enti&egrave;re disposition pour toute information compl&eacute;mentaire.</p>
 <p>Cordialement,</p>
 <p>Mairie de Paris.</p>"),
(3, "mobile_phone_number", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un sms de confirmation vous a &eacute;t&eacute; envoy&eacute; au num&eacute;ro suivant&nbsp;${mobilePhoneNumber}. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
 <p>Nous restons &agrave; votre enti&egrave;re disposition pour toute information compl&eacute;mentaire.</p>
 <p>Cordialement,</p>
 <p>Mairie de Paris.</p>");
 
DELETE FROM ticketing_support_entity ;
INSERT INTO ticketing_support_entity (id_support_entity, name, level, id_unit, id_admin_user, id_domain) VALUES
(1, 'SN2 Facil Famille ', 1, 2, -1, 210), 
(2, 'SN2 Facil Famille tarif. Petite Enfance. ', 2, 21, -1, 210), 
(3, 'SN2 Facil Famille tarif. périscolaire', 2, 20, -1, 210),
(4, 'SN3 DFPE - BEF', 3, 40, -1, 210),
(5, 'SN2 Voirie', 2, 30, -1, 120);

DELETE FROM ticketing_channel ;
INSERT INTO ticketing_channel (id_channel, label, icon_font) VALUES
(1, "Téléphone", "fa fa-phone"),
(2, "Visite", "fa fa-user"),
(3, "E-mail", "fa fa-envelope"),
(4, "Twitter", "fa fa-twitter"),
(5, "Facebook", "fa fa-facebook"),
(99, "Guichet (Web)", "fa fa-desktop");
