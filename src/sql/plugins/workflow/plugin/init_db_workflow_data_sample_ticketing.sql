INSERT INTO workflow_workflow VALUES (301,'Workflow GRU','Workflow GRU','2016-01-13 08:36:34',1,'all');

INSERT INTO workflow_state VALUES (301,'Nouveau','Nouveau',301,1,0,NULL,1);
INSERT INTO workflow_state VALUES (302,'A qualifier','A qualifier',301,0,0,NULL,2);

INSERT INTO workflow_action VALUES (301,'Initialisation','Initialisation de la sollicitation',301,301,302,1,1,0,1,0);

INSERT INTO workflow_task VALUES (301,'taskTicketingGenerateTicketReference',301,1);