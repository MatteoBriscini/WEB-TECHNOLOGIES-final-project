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



## Design and implementation choices

//TODO AGGIUNGERE LINK AI PDF UFFICIALI

### DB structure
