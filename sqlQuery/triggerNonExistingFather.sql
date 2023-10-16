
DELIMITER //
CREATE TRIGGER add_new_categories_with_not_existing_father
AFTER INSERT ON categories FOR EACH ROW BEGIN
IF EXISTS(
		SELECT * FROM categories
		WHERE fatherID != 0 AND fatherID NOT IN (SELECT ID FROM categories)
)THEN 
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'it is not possible to add a child to non existent father';
END IF;
END//
DELIMITER ;