-- ----------------------------
-- Addition of table ticket_address
-- ----------------------------

DROP TABLE IF EXISTS ticketing_ticket_address;
CREATE TABLE ticketing_ticket_address (
id_ticket int(6) NOT NULL,
address varchar(255) NULL,
address_detail varchar(255) NULL,
postal_code varchar(5) NULL,
city varchar(255) NULL,
PRIMARY KEY (id_ticket)
);

ALTER TABLE ticketing_ticket_address ADD CONSTRAINT fk_ticketing_ticket_address FOREIGN KEY (id_ticket)
      REFERENCES ticketing_ticket (id_ticket) ON DELETE CASCADE;