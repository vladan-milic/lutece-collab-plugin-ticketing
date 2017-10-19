
DELETE FROM ticketing_user_title ;
INSERT INTO ticketing_user_title (id_user_title, label) VALUES
(0, ""),
(1, "Mme"),
(2, "M.");

DELETE FROM ticketing_contact_mode ;
INSERT INTO ticketing_contact_mode (id_contact_mode, code, required_inputs, confirmation_msg) VALUES
(1, "email", "email", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un e-mail de confirmation vous a &eacute;t&eacute; envoy&eacute; &agrave; l'adresse suivante&nbsp;${email}. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
 <p>Nous restons &agrave; votre enti&egrave;re disposition pour toute information compl&eacute;mentaire.</p>
 <p>Cordialement,</p>
 <p>Mairie de Paris.</p>"),
(2, "courier", "address, postal_code, city", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un courrier de confirmation vous a &eacute;t&eacute; envoy&eacute; &agrave; l'adresse suivante&nbsp;A COMPLETER. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
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

DELETE FROM ticketing_markings ;
INSERT INTO ticketing_markings VALUES (1, 'NON LU', '#FFFFFF', '#00C0EF');
