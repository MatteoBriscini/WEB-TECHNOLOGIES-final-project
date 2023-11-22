# WEB TECHNOLOGY (final project) 
***Politecnico of Milan [2022-2023]***
<br>
The course requires the students to provide two versions of the same project. <br>
The first version was developed in Java (for Tomcat 10) as a pure HTML website. <br>
The second version is a RIA web app developed in Java and JavaScript, some additional features were required for this version.<br>
Application data are stored using MySQL DB; the DB structure is provided below.

## Project requirements
The provided application has to allow the user to manage a classification taxonomy. <br> 
After logging in, the user accesses a Home page where a hierarchical tree of categories is shown; the categories do not depend on the user but they are shared among all users. <br>
<details>
<summary>show a tree example:</summary>

  * 9 Materiali solidi
  * 91 Materiali inerti
  * 911 Inerti da edilizia
  * 9111 Amianto
  * 91111 Amianto in lastre
  * 91112 Amianto in frammenti
  * 9112 Materiali cementizi
  * 912 Inerti ceramici
  * 9121 Piastrelle
  * 9122 Sanitari

</details><br>
Users can perform several operations on the taxonomy:

  * add a new category
  * remove an existing category (and his sons)
  * copy and paste a sub-tree in a new position
  * cut and paste a sub-tree in a new position
  * search for multiple category names or codes

### Requirements extensions for JavaScript version
Some additional features were required for this version:
 * After the user logs in, the entire application is created with a single page
 * Each user interaction is managed via an asynchronous invocation of the server
 * The function of copying a sub-tree (or cut) is achieved by drag & drop
 * The drop produces an update only on the client side, so the user can delete the operation or save it on the server
 * The user can click on the name of a category to change it

## Design and implementation choices
The official project document, required for the course, is provided below:
 * [Italian document](https://github.com/MatteoBriscini/WEB-TECHNOLOGY-final-project-/blob/master/deliveries/TIWDocumentazione-ita.pdf). 
 * [English document](https://github.com/MatteoBriscini/WEB-TECHNOLOGY-final-project-/blob/master/deliveries/tiwDocumentazione-eng.pdf).
 
in this document, we will focus more on the DB structure as follows.
### DB structure
The DB is structured in 2 tables. <br>
The taxonomy tree is stored via the parent-child self-relationship; a father can have multiple children, but the case of multiple inheritances is not foreseen. <br>
The ER schema is provided below: <br>
![alt text](https://github.com/MatteoBriscini/WEB-TECHNOLOGY-final-project-/blob/master/deliveries/TIW.SchemaER.png) <br> <br>
>**Note**: a partial configuration file for MySQL DB is provided [here](https://github.com/MatteoBriscini/WEB-TECHNOLOGY-final-project-/blob/master/deliveries/DBtest.zip).
#### SQL tables creation code
 * **User table**
   ```
   User CREATE TABLE IF NOT EXIST `usersCredentials` (
 	   `userNum` int NOT NULL AUTO_INCREMENT,
 	   `userID` varchar(45) NOT NULL,
 	   `username` varchar(45) NOT NULL,
 	   `userPWD` varchar(45) NOT NULL,
 	   `userRole` int NOT NULL,
  	  `userMail` varchar(45) NOT NULL,
 	   PRIMARY KEY (`userID`),
 	  	UNIQUE KEY `userID_UNIQUE` (`userID`),
 	  	UNIQUE KEY `username_UNIQUE` (`username`),
 	  	UNIQUE KEY `userMail_UNIQUE` (`userMail`),
 	  	UNIQUE KEY `userNum_UNIQUE` (`userNum`)
   )
   ```
 * **Category table**
   ```
   CREATE TABLE IF NOT EXIST `categories` (
 	   `ID` bigint NOT NULL,
 	   `name` varchar(45) NOT NULL,
 	   `fatherID`bigint DEFAULT NULL,
 	   `lastModifier` varchar(45) NOT NULL,
 	   PRIMARY KEY (`ID`),
 	   UNIQUE KEY `ID_UNIQUE` (`ID`),
 	   KEY `lastModifier_idx` (`lastModifier`),
 	   CONSTRAINT `creatorKey` FOREIGN KEY (`lastModifier`) REFERENCES `usersCredentials` (`userID`)
   )
   ```
#### SQL Triggers
In addition to the constraints on individual attributes and those due to the presence of foreign keys, some triggers have been designed (of the AFTER INSERT, AFTER UPDATE, or AFTER DELETE type).
 * **Not allowed user:** This trigger verifies, when adding a new category, that the user carrying out the operation has the necessary permissions
   ```
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
   ```
 * **related relationship consistency (after insert):** This trigger verifies, after insert of a new category, that all the fatherID codes are related to categories actually saved in the database
   ```
   DELIMITER //
   CREATE TRIGGER add_new_categories_with_not_existing_father 
   AFTER INSERT ON categories FOR EACH ROW BEGIN
   IF EXISTS(
   		SELECT * FROM categories
   		WHERE fatherID != 0 AND fatherID NOT IN (SELECT ID FROM categories)
   )THEN SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'it is not possible to add a child to non existent father';
   END IF;
   END//
   DELIMITER;
   ```
 * **related relationship consistency (after update & delete):**
   ```
   DELIMITER //
   CREATE TRIGGER add_new_categories_with_not_existing_father_after_update
   AFTER UPDATE ON categories FOR EACH ROW BEGIN
   IF EXISTS(
   		SELECT * FROM categories
   		WHERE (@trigger_disable IS NULL OR @trigger_disable!=1) AND fatherID != 0 AND fatherID NOT IN (SELECT ID FROM categories)
   )THEN 
       SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'it is not possible to add a child to non existent father';
   END IF;
   END//
   DELIMITER ;
   ```
   >**Note**: trigger_disable is a session variable, when multiple updates or delete occur is set to 1 to disable triggers. The triggers will be rehabilitated before the last query to verify the database status.
