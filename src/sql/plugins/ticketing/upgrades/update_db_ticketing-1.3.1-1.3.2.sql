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
      
      
-- 
-- This procedure will create a new entry for each resource of type 'TICKET_DOMAIN' and permission 'VIEW'
-- with the same data (except the rbac_id which is compute for each new entry) with the permission 'VIEW_DETAIL'
-- after that it will update all the resource of type 'TICKET_DOMAIN' and permission 'VIEW' with the permission 'VIEW_LIST'
--
SET @id_max := (SELECT MAX(rbac_id) FROM core_admin_role_resource); -- Get the last id of the core_admin_role_resource
DROP PROCEDURE IF EXISTS updateTicketDomainViewRight;
DELIMITER //
CREATE PROCEDURE updateTicketDomainViewRight(id_max int(11))
BEGIN
	DECLARE current_role varchar(50);
    DECLARE current_res_id varchar(50);
	DECLARE done INT DEFAULT FALSE;
	-- Create a cursor to iterate on the resources which have the resource_type 'TICKET_DOMAIN' and the permission 'VIEW'
    DECLARE role_cursor CURSOR FOR 
		SELECT role_key, resource_id FROM core_admin_role_resource WHERE resource_type = 'TICKET_DOMAIN' AND permission = 'VIEW';
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    OPEN role_cursor;
	-- Iterate on all element in the cursor
    role_loop: LOOP
		FETCH role_cursor INTO current_role, current_res_id;
        CASE 
			WHEN done = TRUE THEN LEAVE role_loop;
			-- Insert a new entry of the current role with the new right 'VIEW_DETAIL' for the resource 'TICKET_DOMAIN'
			ELSE INSERT INTO core_admin_role_resource VALUES ((@id_max := (@id_max + 1)), current_role, 'TICKET_DOMAIN', current_res_id, 'VIEW_DETAIL'); 
        END CASE;
    END LOOP;
    CLOSE role_cursor;
	-- After creating the new entries update permission of the first ones
    UPDATE core_admin_role_resource SET permission = 'VIEW_LIST' WHERE resource_type = 'TICKET_DOMAIN' AND permission = 'VIEW';
END; //
CALL updateTicketDomainViewRight(@id_max);

-- 
-- Add new column required_inputs in ticketing_contact_mode
--
UPDATE ticketing_ticket SET id_contact_mode=1 WHERE id_contact_mode>1;
DELETE FROM ticketing_contact_mode WHERE id_contact_mode>1;
ALTER TABLE ticketing_contact_mode ADD COLUMN required_inputs varchar(150) NULL;
UPDATE ticketing_contact_mode SET required_inputs='email' WHERE id_contact_mode=1;
INSERT INTO ticketing_contact_mode (id_contact_mode, code, required_inputs, confirmation_msg) VALUES
(2, "courier", "address, postal_code, city", "<p>Bonjour&nbsp;${userTitle} ${lastName},</p>
 <p>Nous avons bien re&ccedil;u votre demande et nous vous remercions de votre confiance.</p>
 <p>Un courrier de confirmation vous a &eacute;t&eacute; envoy&eacute; &agrave; l'adresse suivante&nbsp;A COMPLETER. Il contient un num&eacute;ro de suivi qui vous sera demand&eacute; au 3975 pour suivre son &eacute;tat d'avancement. Il est &eacute;galement disponible dans votre espace Compte Parisien.</p>
 <p>Nous restons &agrave; votre enti&egrave;re disposition pour toute information compl&eacute;mentaire.</p>
 <p>Cordialement,</p>
 <p>Mairie de Paris.</p>");

