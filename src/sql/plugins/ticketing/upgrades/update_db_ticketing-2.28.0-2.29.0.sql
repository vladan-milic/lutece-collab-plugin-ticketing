CREATE TABLE `ticketing_file_blob` (
	`id_file_blob` INT(11) NOT NULL,
	`creation_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	`id_file` INT(11) NOT NULL,
	`id_blob` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`id_file_blob`)
);

create index workflow_resource_history_PERF02 on workflow_resource_history (id_workflow, id_resource, resource_type);
create index core_admin_role_resource_PERF01 on core_admin_role_resource (resource_type, resource_id, permission);
create index ticketing_ticket_PERF02 on ticketing_ticket (ticket_reference);
create index core_admin_user_field_PERF01 on core_admin_user_field (id_user);

ALTER TABLE ticketing_channel ADD COLUMN flag_mandatory_ticket_comment BOOLEAN;