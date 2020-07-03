-- Création du droit
DELETE FROM core_admin_right WHERE id_right = 'TICKETING_MANAGEMENT_ACTION_BUTTON';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('TICKETING_MANAGEMENT_ACTION_BUTTON', 'ticketing.adminFeature.ManageActionButton.name', 2, 'jsp/admin/plugins/ticketing/admin/ManageActionButton.jsp', 'ticketing.adminFeature.ManageActionButton.description', 0, 'ticketing', 'APPLICATIONS', NULL, NULL, 4);

-- Ajout du droit à l'admin
DELETE FROM core_user_right WHERE id_right = 'TICKETING_MANAGEMENT_ACTION_BUTTON';
INSERT INTO core_user_right (id_right,id_user) VALUES ('TICKETING_MANAGEMENT_ACTION_BUTTON',1);

-- Création de la table des groupes d'action
CREATE TABLE ticketing_groupe_action (
  `id_groupe` int(6) NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `libelle_identifiant` varchar(50) NOT NULL,
  `cle` varchar(100) NOT NULL,
  `description` varchar(150) NOT NULL,
  `ordre` int(11) NOT NULL
);

-- Création de la table des couleurs
CREATE TABLE ticketing_couleur_bouton (
  `id_couleur` varchar(50) NOT null PRIMARY KEY,
  `couleur` varchar(50) NOT NULL
);

-- Création de la table des boutons d'action
CREATE TABLE ticketing_param_bouton_action (
  `id_param` int(6) NOT null AUTO_INCREMENT PRIMARY KEY,
  `id_action` int(6) NOT NULL,
  `id_couleur` varchar(50) NOT NULL,
  `ordre` int(11) NOT NULL,
  `icone` varchar(50) NOT NULL,
  `id_groupe` int(6) NOT NULL,
  FOREIGN KEY (id_action) REFERENCES workflow_action(id_action),
  FOREIGN KEY (id_groupe) REFERENCES ticketing_groupe_action(id_groupe),
  FOREIGN KEY (id_couleur) REFERENCES ticketing_couleur_bouton(id_couleur)
);

-- Insert des couleurs
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Blanc', '#FFFFFF');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Noir','#000000');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Rouge','#FF0000');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Vert foncé','#008000');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Vert clair','#00FF00');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Bleu foncé','#000080');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Bleu clair','#0000FF');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Rose','#FF00FF');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Mauve','#800080');
INSERT INTO ticketing_couleur_bouton (id_couleur, couleur) VALUES( 'Jaune','#FFFF00');


-- Insert du groupe "non configurés"
INSERT into ticketing_groupe_action (id_groupe, libelle_identifiant, cle, description, ordre) VALUES(1, 'NON CONFIGURES', 'Non configurés' , 'Les boutons d''action du workflow qui n''ont pas encore été configurés ', 1);
