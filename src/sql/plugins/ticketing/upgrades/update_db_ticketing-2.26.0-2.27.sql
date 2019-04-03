ALTER TABLE workflow_task_ticketing_email_external_user_config ADD default_subject varchar(255) NULL;
ALTER TABLE workflow_ticketing_email_external_user ADD email_subject varchar(255) NULL ;

ALTER TABLE `ticketing_ticket` ADD COLUMN `demand_id` INT(6) NOT NULL DEFAULT '0' AFTER `id_marking`;

update ticketing_category set demand_id=101;

update ticketing_ticket a inner join ticketing_category b on a.id_ticket_category = b.id_category set a.demand_id = b.demand_id;


/*** Shinx Daemon - BEGIN ****/

DELETE FROM core_datastore WHERE core_datastore.entity_key = "core.daemon.sphinxDaemon.interval";
DELETE FROM core_datastore WHERE core_datastore.entity_key = "core.daemon.sphinxDaemon.onStartUp";
INSERT INTO core_datastore (`entity_key`, `entity_value`) VALUES
('core.daemon.sphinxDaemon.interval', '6000'),
('core.daemon.sphinxDaemon.onStartUp', 'true');

/*** Shinx Daemon - END ****/
