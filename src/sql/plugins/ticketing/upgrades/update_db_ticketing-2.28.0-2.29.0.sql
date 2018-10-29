CREATE TABLE `ticketing_file_blob` (
	`id_file_blob` INT(11) NOT NULL,
	`creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`id_file` INT(11) NOT NULL,
	`id_blob` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id_file_blob`)
);