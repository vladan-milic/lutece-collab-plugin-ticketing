
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
-- Structure for table ticketing_ticket
--

DROP TABLE IF EXISTS ticketing_ticket;
CREATE TABLE ticketing_ticket (
id_ticket int(6) NOT NULL,
ticket_reference varchar(20) NULL, 
guid varchar(255) NULL,
id_customer varchar(50) NULL,
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
is_read int(1) NOT NULL default '0',
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
-- Structure for table ticketing_ticket_category
--

DROP TABLE IF EXISTS ticketing_ticket_category;
CREATE TABLE ticketing_ticket_category (
id_ticket_category int(6) NOT NULL,
id_ticket_domain int(11) NOT NULL,
label varchar(50) NOT NULL default '',
id_workflow INT NOT NULL default '0',
category_code varchar(50) NULL,
id_unit int(6) NOT NULL default '0',
inactive int(1)  NOT NULL default '0',
category_precision VARCHAR(150) NULL,
help_message VARCHAR(500) NULL,
category_order INT(6) DEFAULT 0 NOT NULL,
PRIMARY KEY (id_ticket_category)
);

--
-- Structure for table ticketing_ticket_domain
--

DROP TABLE IF EXISTS ticketing_ticket_domain;
CREATE TABLE ticketing_ticket_domain (
id_ticket_domain int(6) NOT NULL,
id_ticket_type int(11) NOT NULL,
label varchar(50) NOT NULL default '',
inactive int(1)  NOT NULL default '0',
PRIMARY KEY (id_ticket_domain)
);

--
-- Structure for table ticketing_ticket_type
--

DROP TABLE IF EXISTS ticketing_ticket_type;
CREATE TABLE ticketing_ticket_type (
id_ticket_type int(6) NOT NULL,
label varchar(50) NOT NULL default '',
reference_prefix varchar(3) NOT NULL,
inactive int(1)  NOT NULL default '0',
demand_type_id int(6) NOT NULL default '0',
PRIMARY KEY (id_ticket_type)
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
-- Structure for table ticketing_types_reponses
--
--
-- Structure for table ticketing_Model_reponses
--

DROP TABLE IF EXISTS ticketing_model_reponses;
CREATE TABLE `ticketing_model_reponses` (
  `id_model_response` int(6) NOT NULL,
  `id_ticket_domain` int(11) NOT NULL DEFAULT '0',
  `title` varchar(500) NOT NULL DEFAULT '',
  `reponse` text,
  `keyword` text
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
inactive int(1)  NOT NULL default '0',
PRIMARY KEY (id_channel)
);

--
-- Structure for table ticketing_ticket_category_input
--

DROP TABLE IF EXISTS ticketing_ticket_category_input;

CREATE TABLE ticketing_ticket_category_input (
    id_ticket_category int(6) NOT NULL,
    id_input int(6) NOT NULL,
    pos int(6) NOT NULL default '0',
    PRIMARY KEY (id_ticket_category, id_input)
);


ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_user_title FOREIGN KEY (id_user_title)
      REFERENCES ticketing_user_title (id_user_title) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_category FOREIGN KEY (id_ticket_category)
      REFERENCES ticketing_ticket_category (id_ticket_category) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_contact_mode FOREIGN KEY (id_contact_mode)
      REFERENCES ticketing_contact_mode (id_contact_mode) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket ADD CONSTRAINT fk_ticketing_ticket_channel FOREIGN KEY (id_channel)
      REFERENCES ticketing_channel (id_channel) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket_domain ADD CONSTRAINT fk_ticketing_ticket_domain_type FOREIGN KEY (id_ticket_type)
      REFERENCES ticketing_ticket_type (id_ticket_type) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_ticket_category ADD CONSTRAINT fk_ticketing_ticket_category_domain FOREIGN KEY (id_ticket_domain)
      REFERENCES ticketing_ticket_domain (id_ticket_domain) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_instant_response ADD CONSTRAINT fk_ticketing_instant_response_category FOREIGN KEY (id_ticket_category)
      REFERENCES ticketing_ticket_category (id_ticket_category) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE ticketing_indexer_action ADD CONSTRAINT fk_ticketing_indexer_action_ticket FOREIGN KEY (id_ticket)
      REFERENCES ticketing_ticket (id_ticket) ON DELETE RESTRICT ON UPDATE RESTRICT;

      