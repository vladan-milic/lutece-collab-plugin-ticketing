
--
-- Structure for table ticketing_ticket
--

DROP TABLE IF EXISTS ticketing_ticket;
CREATE TABLE ticketing_ticket (
id_ticket int(6) NOT NULL,
ticket_reference varchar(20) NULL, 
guid varchar(255) NULL,
id_customer varchar(20) NULL, 
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
priority int(6) NOT NULL default '0',
criticality int(6) NOT NULL default '0',
date_update timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
date_create timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
date_close timestamp NULL,
id_admin_user int(11) NOT NULL default '-1',
id_unit int(11) NOT NULL default '0', 
user_message long varchar NULL,
url varchar(4096) NULL,
id_channel int(11) NOT NULL default '0',
PRIMARY KEY (id_ticket)
);

--
-- Structure for table ticketing_ticket_type
--

DROP TABLE IF EXISTS ticketing_ticket_type;
CREATE TABLE ticketing_ticket_type (
id_ticket_type int(6) NOT NULL,
label varchar(50) NOT NULL default '',
reference_prefix varchar(3) NOT NULL,
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
id_unit int(6) NOT NULL default '0',
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

--
-- Structure for table ticketing_support_entity
--
DROP TABLE IF EXISTS ticketing_support_entity;
CREATE TABLE ticketing_support_entity(
id_support_entity int(6) NOT NULL,
name varchar(50) NOT NULL,
level int(2) NOT NULL,
id_unit int(6) NOT NULL,
id_admin_user int(6) NULL,
id_domain int(6) NOT NULL,
PRIMARY KEY (id_support_entity)
);

--
-- Structure for table ticketing_instant_response
--

DROP TABLE IF EXISTS ticketing_instant_response;
CREATE TABLE ticketing_instant_response (
id_instant_response int(6) NOT NULL,
id_ticket_category int(11) NOT NULL default '0',
subject long varchar NULL ,
date_create timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
id_unit int(11) NOT NULL default '0', 
id_admin_user int(11) NOT NULL default '0',
id_channel int(11) NOT NULL default '0',
PRIMARY KEY (id_instant_response)
);

--
-- Structure for table ticketing_channel
--

DROP TABLE IF EXISTS ticketing_channel;
CREATE TABLE ticketing_channel (
id_channel int(6) NOT NULL,
label varchar(50) NOT NULL default '',
PRIMARY KEY (id_channel)
);

--
-- Structure for table ticketing_indexer_action
--

DROP TABLE IF EXISTS ticketing_indexer_action;
CREATE TABLE ticketing_indexer_action (
  id_action INT DEFAULT 0 NOT NULL,
  id_ticket INT DEFAULT 0 NOT NULL,
  id_task INT DEFAULT 0 NOT NULL ,
  PRIMARY KEY (id_action)
  );
CREATE INDEX ticketing_id_indexer_task ON ticketing_indexer_action (id_task);



      