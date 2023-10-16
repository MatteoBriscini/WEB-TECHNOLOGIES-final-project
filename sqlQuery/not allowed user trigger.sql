
DELIMITER //
CREATE TRIGGER add_new_category_by_not_allowed_user 
AFTER INSERT ON categories FOR EACH ROW BEGIN
IF EXISTS(
	SELECT * FROM categories JOIN usersCredentials ON lastModifier = userID
	WHERE userRole<1
)THEN 
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'you do not have autorization for this operation';
END IF;
END//
DELIMITER ;