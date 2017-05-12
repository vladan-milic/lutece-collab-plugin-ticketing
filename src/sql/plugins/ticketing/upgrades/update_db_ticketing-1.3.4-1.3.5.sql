-- ----------------------------
-- Changing order of ticket_type
-- ----------------------------
ALTER TABLE ticketing_ticket_type ADD COLUMN type_order INT(6) NOT NULL DEFAULT 0;

-- Set order value to -1 for deactivated types
UPDATE ticketing_ticket_type SET type_order = -1 WHERE inactive = 1;

-- init of Types orders with increasing sequence
SET @order:=0;
update ticketing_ticket_type SET type_order=@order:=@order+1  WHERE inactive <> 1;


-- --------------------------------
-- Changing order of ticket_domains
-- --------------------------------
ALTER TABLE ticketing_ticket_domain ADD COLUMN domain_order INT(6) NOT NULL DEFAULT 0;

-- Set order value to -1 for deactivated domains
UPDATE ticketing_ticket_domain SET type_order = -1 WHERE inactive = 1;

-- init of Domain orders with increasing sequence
SET @order:=0;
update ticketing_ticket_domain SET domain_order=@order:=@order+1  WHERE inactive <> 1;