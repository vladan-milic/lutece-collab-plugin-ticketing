-- Structure for table ticketing_viewing 
-- 
 
DROP TABLE IF EXISTS ticketing_viewing; 
CREATE TABLE ticketing_viewing ( 
id_viewing int(6) NOT NULL, 
title varchar(50) default '', 
message varchar(5000) default '', 
buton_label varchar(50) default '', 
channel varchar(50) default '', 
contact_mode varchar(50) default '', 
civility varchar(50) default '', 
domain varchar(50) default '', 
thematic varchar(50) default '', 
location varchar(50) default '', 
PRIMARY KEY (id_viewing) 
);

ALTER TABLE ticketing.ticketing_category ADD is_manageable BOOL DEFAULT false NOT NULL;