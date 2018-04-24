update ticketing_category set demand_id=101;

ALTER TABLE workflow_task_ticketing_email_external_user_config ADD default_subject varchar(255) NULL;
ALTER TABLE workflow_ticketing_email_external_user ADD email_subject varchar(255) NULL ;
