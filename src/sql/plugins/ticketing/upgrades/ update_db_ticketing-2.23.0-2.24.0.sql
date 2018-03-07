/** ADD new column on the ticketing_category table **/
ALTER TABLE ticketing_category ADD icon_font VARCHAR(50) NULL default '';

/** INSERT default form **/
DELETE FROM ticketing_form WHERE id_form = 1;
INSERT INTO ticketing_form (id_form,title,message,button_label,`connection`) VALUES (1,'Demandes & r&eacute;clamations','','J''envoie ma demande',1);
INSERT INTO ticketing_formentry (id_formentry,id_form,id_champ,hidden,mandatory,`hierarchy`,default_value) VALUES 
(1,1,'user_title',0,1,0,''),
(2,1,'last_name',0,1,0,''),
(3,1,'first_name',0,1,0,''),
(4,1,'email',0,1,0,''),
(5,1,'phone_numbers',0,1,0,''),
(6,1,'contact_mode',1,0,0,''),
(7,1,'comment',0,1,0,''),
(8,1,'category_level_1',0,1,0,'-1'),
(9,1,'category_level_2',0,1,0,NULL),
(10,1,'category_level_3',0,1,0,NULL);

