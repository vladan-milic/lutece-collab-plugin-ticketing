
--
-- Structure for table ticketing_ticket
--

DROP TABLE IF EXISTS ticketing_ticket;
CREATE TABLE ticketing_ticket (
id_ticket int(6) NOT NULL,
id_user_title int(11) NOT NULL default '0',
firstname varchar(50) NOT NULL default '',
lastname varchar(50) NOT NULL default '',
email varchar(255) NOT NULL default '',
fixed_phone_number varchar(50) NOT NULL default '',
mobile_phone_number varchar(50) NOT NULL default '',
id_ticket_category int(11) NOT NULL default '0',
id_contact_mode int(11) NOT NULL default '0',
ticket_comment long varchar NULL ,
ticket_status int(11) NOT NULL default '0',
ticket_status_text varchar(255) NULL default '',
date_update timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
date_create timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
PRIMARY KEY (id_ticket)
);

--
-- Structure for table ticketing_ticket_type
--

DROP TABLE IF EXISTS ticketing_ticket_type;
CREATE TABLE ticketing_ticket_type (
id_ticket_type int(6) NOT NULL,
label varchar(50) NOT NULL default '',
PRIMARY KEY (id_ticket_type)
);

--
-- Structure for table ticketing_ticket_domain
--

DROP TABLE IF EXISTS ticketing_ticket_domain;
CREATE TABLE ticketing_ticket_domain (
id_ticket_domain int(6) NOT NULL,
id_ticket_type int(11) NOT NULL default '0',
label varchar(50) NOT NULL default '',
PRIMARY KEY (id_ticket_domain)
);

--
-- Structure for table ticketing_ticket_category
--

DROP TABLE IF EXISTS ticketing_ticket_category;
CREATE TABLE ticketing_ticket_category (
id_ticket_category int(6) NOT NULL,
id_ticket_domain int(11) NOT NULL default '0',
label varchar(50) NOT NULL default '',
id_workflow INT NOT NULL default '0',
id_ticket_form int(6) NOT NULL default '0',
category_code varchar(50) NULL,
PRIMARY KEY (id_ticket_category)
);

--
-- Structure for table ticketing_user_title
--

DROP TABLE IF EXISTS ticketing_user_title;
CREATE TABLE ticketing_user_title (
id_user_title int(6) NOT NULL,
label varchar(50) NOT NULL default '',
PRIMARY KEY (id_user_title)
);

--
-- Structure for table ticketing_form
--

DROP TABLE IF EXISTS ticketing_ticket_form;

CREATE TABLE ticketing_ticket_form (
	id_form int(6) NOT NULL,
	title varchar(255) NOT NULL default '',
	description long varchar NOT NULL,
	PRIMARY KEY (id_form)
);

--
-- Structure for table ticketing_ticket_response
--

DROP TABLE IF EXISTS ticketing_ticket_response;

CREATE TABLE ticketing_ticket_response (
	id_ticket INT(6) NOT NULL,
	id_response INT(6) NOT NULL,
	PRIMARY KEY (id_ticket, id_response)
);

--
-- Structure for table ticketing_contact_mode
--

DROP TABLE IF EXISTS ticketing_contact_mode;
CREATE TABLE ticketing_contact_mode (
id_contact_mode int(6) NOT NULL,
label varchar(50) NOT NULL default '',
confirmation_msg long varchar NULL,
PRIMARY KEY (id_contact_mode)
);


      