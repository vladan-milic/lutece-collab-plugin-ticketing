
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

--
-- Structure for table ticketing_ticket_address
--

DROP TABLE IF EXISTS ticketing_ticket_address;
CREATE TABLE ticketing_ticket_address (
id_ticket int(6) NOT NULL,
address varchar(255) NULL,
address_detail varchar(255) NULL,
postal_code varchar(5) NULL,
city varchar(255) NULL,
PRIMARY KEY (id_ticket)
);

--
-- Structure for table ticketing_ticket
--

DROP TABLE IF EXISTS ticketing_ticket;
CREATE TABLE ticketing_ticket (
id_ticket int(6) NOT NULL,
ticket_reference varchar(20) NULL, 
guid varchar(255) NULL,
id_customer varchar(100) NULL,
id_user_title int(11) NOT NULL,
firstname varchar(50) NOT NULL,
lastname varchar(50) NOT NULL,
email varchar(255) NOT NULL,
fixed_phone_number varchar(50) NULL default '',
mobile_phone_number varchar(50) NULL default '',
id_ticket_category int(11) NOT NULL,
id_contact_mode int(11) NOT NULL,
ticket_comment long varchar NULL ,
ticket_status int(11) NOT NULL default '0',
ticket_status_text varchar(255) NULL default '',
priority int(6) NOT NULL default '0',
criticality int(6) NOT NULL default '0',
date_update timestamp NOT NULL,
date_create timestamp NOT NULL,
date_close timestamp NULL,
id_admin_user int(11) NOT NULL default '-1',
id_unit int(11) NOT NULL default '0', 
user_message long varchar NULL,
url varchar(4096) NULL,
id_channel int(11) NULL,
id_assigner_user int(11) NOT NULL default '-1',
id_assigner_unit int(11) NOT NULL default '0', 
nomenclature varchar(3) NULL,
id_marking INT(11) DEFAULT '0',
PRIMARY KEY (id_ticket)
);

--
-- Structure for table ticketing_instant_response
--

DROP TABLE IF EXISTS ticketing_instant_response;
CREATE TABLE ticketing_instant_response (
id_instant_response int(6) NOT NULL,
id_ticket_category int(11) NOT NULL,
subject long varchar NULL ,
date_create timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
id_unit int(11) NOT NULL default '0', 
id_admin_user int(11) NOT NULL default '0',
id_channel int(11) NOT NULL,
PRIMARY KEY (id_instant_response)
);

--
-- Structure for table ticketing_category
--
DROP TABLE IF EXISTS ticketing_category;
CREATE TABLE ticketing_category (
id_category int(6) NOT NULL,
id_parent int(6) NOT NULL,
label varchar(255) NOT NULL default '',
n_order int(6) DEFAULT 0 NOT NULL,
code varchar(50) NULL,
id_default_assignee_unit int(6) NOT NULL default '0',
id_category_type int(6) NOT NULL default '0',
id_workflow int(6) NOT NULL default '0',
demand_id int(6) NOT NULL default '0',
help_message varchar(500) NULL,
is_manageable int(1) DEFAULT 0 NOT NULL,
inactive int(1)  NOT NULL default '0',
icon_font VARCHAR(50)  NULL default '',
PRIMARY KEY (id_category)
);

--
-- Structure for table ticketing_category_type
--
DROP TABLE IF EXISTS ticketing_category_type;
CREATE TABLE ticketing_category_type (
id_category_type int(6) NOT NULL,
label varchar(255) NOT NULL default '',
depth int(6) DEFAULT 0 NOT NULL,
PRIMARY KEY (id_category_type)
);

--
-- Structure for table ticketing_user_title
--

DROP TABLE IF EXISTS ticketing_user_title;
CREATE TABLE ticketing_user_title (
id_user_title int(6) NOT NULL,
label varchar(50) NOT NULL default '',
inactive int(1)  NOT NULL default '0',
PRIMARY KEY (id_user_title)
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
code varchar(50) NOT NULL default '',
required_inputs varchar(150) NULL,
confirmation_msg long varchar NULL,
inactive int(1)  NOT NULL default '0',
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
-- Structure for table ticketing_Model_reponses
--

DROP TABLE IF EXISTS ticketing_model_reponses;
CREATE TABLE `ticketing_model_reponses` (
  `id_model_response` int(6) NOT NULL,
  `title` varchar(500) NOT NULL DEFAULT '',
  `reponse` text,
  `keyword` text,
  label_ticket_domain VARCHAR(50) DEFAULT ''
);

ALTER TABLE `ticketing_model_reponses` ADD PRIMARY KEY (`id_model_response`);

--
-- Structure for table ticketing_channel
--

DROP TABLE IF EXISTS ticketing_channel;
CREATE TABLE ticketing_channel (
id_channel int(6) NOT NULL,
label varchar(50) NOT NULL default '',
icon_font varchar(50) NULL default '',
flag_mandatory_ticket_comment boolean,
inactive int(1)  NOT NULL default '0',
PRIMARY KEY (id_channel)
);

--
-- Structure for table ticketing_category_input
--

DROP TABLE IF EXISTS ticketing_category_input;
CREATE TABLE ticketing_category_input (
    id_category int(6) NOT NULL,
    id_input int(6) NOT NULL,
    pos int(6) NOT NULL default '0',
    PRIMARY KEY (id_category, id_input)
);

--
-- Structure for table ticketing_markings
--
DROP TABLE IF EXISTS ticketing_markings;
CREATE TABLE ticketing_markings (
	id_marking INT(6) NOT NULL,
	title VARCHAR(500) NOT NULL DEFAULT '',
	label_color VARCHAR(50),
	background_color VARCHAR(50),
	PRIMARY KEY (id_marking)
);

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

--
-- Structure for table ticketing_form
--

DROP TABLE IF EXISTS ticketing_form;
CREATE TABLE ticketing_form (
id_form int(6) NOT NULL,
title long varchar,
message long varchar,
button_label long varchar,
connection SMALLINT,
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
hidden SMALLINT default '0' NOT NULL,
mandatory SMALLINT default '0' NOT NULL,
hierarchy int(11) default '0' NOT NULL,
default_value varchar(500) default '',
PRIMARY KEY (id_formentry)
);


ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_user_title FOREIGN KEY (id_user_title)
      REFERENCES ticketing_user_title (id_user_title) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_category FOREIGN KEY (id_ticket_category)
      REFERENCES ticketing_category (id_category) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_contact_mode FOREIGN KEY (id_contact_mode)
      REFERENCES ticketing_contact_mode (id_contact_mode) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_channel FOREIGN KEY (id_channel)
      REFERENCES ticketing_channel (id_channel) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_instant_response ADD CONSTRAINT fk_ticketing_instant_response_category FOREIGN KEY (id_ticket_category)
      REFERENCES ticketing_category (id_category) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_indexer_action ADD CONSTRAINT fk_ticketing_indexer_action_ticket FOREIGN KEY (id_ticket)
      REFERENCES ticketing_ticket (id_ticket) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket_address ADD CONSTRAINT fk_ticketing_ticket_address FOREIGN KEY (id_ticket)
      REFERENCES ticketing_ticket (id_ticket) ON DELETE CASCADE;

DROP FUNCTION IF EXISTS getCategoryChilds;

--
-- Structure for table ticketing_form_category
--
DROP TABLE IF EXISTS ticketing_form_category;
CREATE TABLE ticketing_form_category (id_form int(11) default '0' NOT NULL, id_category int(11) default '0' NOT NULL, PRIMARY KEY (id_form, id_category));

      