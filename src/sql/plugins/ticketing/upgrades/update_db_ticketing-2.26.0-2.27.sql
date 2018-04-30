update ticketing_category set demand_id=101;

ALTER TABLE workflow_task_ticketing_email_external_user_config ADD default_subject varchar(255) NULL;
ALTER TABLE workflow_ticketing_email_external_user ADD email_subject varchar(255) NULL ;

ALTER TABLE `ticketing_ticket` ADD COLUMN `demand_id` INT(6) NOT NULL DEFAULT '0' AFTER `id_marking`;
update ticketing_ticket a inner join ticketing_category b on a.id_ticket_category = b.id_category set a.demand_id = b.demand_id;
