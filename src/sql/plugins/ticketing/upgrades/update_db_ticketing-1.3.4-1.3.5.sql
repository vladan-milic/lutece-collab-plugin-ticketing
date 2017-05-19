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
UPDATE ticketing_ticket_domain SET domain_order = -1 WHERE inactive = 1;

-- init of Domain orders with increasing sequences grouped by ticket_type
UPDATE ticketing_ticket_domain d,
(select id_ticket_domain,  @rownum := CASE WHEN @id_type <> id_ticket_type  THEN concat(left(@id_type:=id_ticket_type, 0), 1) ELSE @rownum + 1 END AS domain_order
FROM (SELECT  @id_type := 0) y,(SELECT @rownum := 0) z,
(SELECT *  from ticketing_ticket_domain d WHERE d.inactive <> 1 ORDER BY d.id_ticket_type, d.id_ticket_domain ) x) dom
SET d.domain_order = dom.domain_order WHERE  d.id_ticket_domain = dom.id_ticket_domain;


-- --------------------------------
-- Changing order of ticket_categories
-- --------------------------------

-- Set order value to -1 for deactivated domains
UPDATE ticketing_ticket_category SET category_order = -1 WHERE inactive = 1;

-- init of Domain orders with increasing sequences grouped by ticket_type
UPDATE ticketing_ticket_category c,
(select id_ticket_category, id_ticket_domain, @rownum := CASE WHEN @id_domaine <> id_ticket_domain THEN concat(left(@id_domaine:=id_ticket_domain, 0), 1) ELSE @rownum + 1 END AS category_order
FROM (SELECT  @id_domaine := 0) y,(SELECT @rownum := 0) z,
(SELECT *  from ticketing_ticket_category c WHERE c.inactive <> 1 ORDER BY c.id_ticket_domain, c.id_ticket_category ) x) cat
SET c.category_order = cat.category_order WHERE  c.id_ticket_category = cat.id_ticket_category;