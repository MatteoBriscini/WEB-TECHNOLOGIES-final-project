package it.polimi.tiw.beams;

import it.polimi.tiw.dao.UserDAO;

import java.sql.Connection;

public class User {
    private String username;
    private String email;
    private int role;
    private String userID;

    public User(String username){
        this.username=username;
    }
    public User(){}

    /*
     *       get methods
     */
    public String getUsername() {
        return username;
    }
    public int getRole() {
        return role;
    }
    public String getUserID() {
        return userID;
    }
    public String getEmail() {
        return email;
    }

    /*
     *       set methods
     */
    public void setRole(int role) {
        this.role = role;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
