
--
-- Structure for table ticketing_form
--

DROP TABLE IF EXISTS ticketing_form;
CREATE TABLE ticketing_form (
id_form int(6) NOT NULL,
title long varchar,
message long varchar,
button_label int(11) default '0',
PRIMARY KEY (id_form)
);

--
-- Structure for table ticketing_formentry
--

DROP TABLE IF EXISTS ticketing_formentry;
CREATE TABLE ticketing_formentry (
id_formentry int(6) NOT NULL,
id_form int(11) default '0' NOT NULL,
id_champ varchar(50) default '' NOT NULL,
hidden SMALLINT NOT NULL,
mandatory SMALLINT NOT NULL,
hierarchy int(11) default '0' NOT NULL,
PRIMARY KEY (id_formentry)
);
