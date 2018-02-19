-- This function must be pass manualy. Ant don't support it

DROP FUNCTION IF EXISTS getCategoryChilds;
DELIMITER //
CREATE FUNCTION getCategoryChilds(categoryName varchar(1000))
RETURNS varchar(1000)  
 BEGIN 
	DECLARE rootId INT;
    DECLARE sTemp VARCHAR(1000);  
    DECLARE sTempChd VARCHAR(1000); 
	SELECT id_category INTO rootId FROM ticketing_category WHERE label = categoryName;
    SET sTemp = '$';  
    SET sTempChd =cast(rootId as CHAR);  
    WHILE sTempChd is not null DO  
         SET sTemp = concat(sTemp,',',sTempChd); 
         SELECT group_concat(id_category) INTO sTempChd FROM ticketing_category where FIND_IN_SET(id_parent,sTempChd)>0;  
    END WHILE;  
    RETURN sTemp;  
END //
DELIMITER ;