# WEB TECHNOLOGY (final project) 
***Politecnico of Milan [2022-2023]***
<br>
The course requires the students to provide two versions of the same project. <br>
The first version was developed in Java (for Tomcat 10) as a pure HTML website. <br>
The second version is a RIA web app developed in Java and JavaScript, some additional feature was required for this version.<br>
application data are stored using MySQL DB, the DB structure is provided below.

## Project requirements
the provided application has to allow the user to manage a classification taxonomy. <br> 
after logging in, the user accesses a Home page where a hierarchical tree of categories is shown, the categories do not depend on the user but are share among all users <br>
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
Users are allowed to several operations on the taxonomy:
  * add a new category
  * remove an existing category (and his sons)
  * copy and paste a sub-tree in a new position
  * cut and paste a sub-tree in a new position
  * search for multiple category names or code

### Requirements extensions for the version with JavaScript
Some additional feature was required for this version:
 * After the user logs in, the entire application is created with a single page
 * Each user interaction is managed via an asynchronous invocation of the server
 * The function of copying a subtree (or cut) is achieved by drag & drop
 * The drop produces an update only on the client side, so the user can delete the operation or save it on the server
 * The user can click on the name of a category to change it

## Design and implementation choices
You can find the official project document required for the course in the links below:
 * [here](https://github.com/MatteoBriscini/WEB-TECHNOLOGY-final-project-/blob/master/deliveries/TIWDocumentazione-ita.pdf). 
 * [here](https://github.com/MatteoBriscini/WEB-TECHNOLOGY-final-project-/blob/master/deliveries/tiwDocumentazione-eng.pdf).
Here we will focus more on the DB structure as follows.
### DB structure
The DB is structured in 2 tables, the taxonomy is represented in the auto-relationship between father categories with his direct soons. <be>
the ER schema is provided below:
![alt text](https://github.com/MatteoBriscini/WEB-TECHNOLOGY-final-project-/blob/master/deliveries/TIW.SchemaER.png)
####
