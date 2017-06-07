-- ----------------------------------------------------------
-- Change relationship beetween Model responses and Domains
-- now using domain label instead of id
-- ----------------------------------------------------------
ALTER TABLE ticketing_model_reponses ADD COLUMN label_ticket_domain VARCHAR(50) DEFAULT '';
UPDATE ticketing_model_reponses mr, ticketing_ticket_domain d SET mr.label_ticket_domain = d.label WHERE mr.id_ticket_domain = d.id_ticket_domain;

ALTER TABLE ticketing_model_reponses DROP COLUMN id_ticket_domain;
