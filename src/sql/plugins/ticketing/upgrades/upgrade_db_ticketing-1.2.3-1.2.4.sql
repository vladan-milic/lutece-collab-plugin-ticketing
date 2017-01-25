-- ----------------------------
-- Changing order of categories
-- ----------------------------

-- Set order value to -1 for deactivated categories
ALTER TABLE ticketing_ticket_category add column category_order INT(6) DEFAULT 0 NOT NULL;
UPDATE ticketing_ticket_category SET category_order = -1 WHERE inactive = 1; 
-- init of Category orders with increasing sequence
SET @order:=0;
update ticketing_ticket_category SET category_order=@order:=@order+1  WHERE inactive <> 1;
