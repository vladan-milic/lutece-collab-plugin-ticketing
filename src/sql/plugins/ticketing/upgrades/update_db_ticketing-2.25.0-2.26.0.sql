-- Change name
ALTER TABLE `ticketing_model_reponses`
	CHANGE COLUMN `id_ticket_category` `label_ticket_domain` VARCHAR(50) NULL DEFAULT '' COLLATE 'utf8_unicode_ci' AFTER `keyword`; 