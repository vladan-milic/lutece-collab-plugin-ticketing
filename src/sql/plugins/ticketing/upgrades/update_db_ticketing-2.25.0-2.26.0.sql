-- Change name
ALTER TABLE `ticketing_model_reponses`
	CHANGE COLUMN `label_ticket_domain` `id_ticket_category` VARCHAR(50) NULL DEFAULT '' COLLATE 'utf8_unicode_ci' AFTER `keyword`; 