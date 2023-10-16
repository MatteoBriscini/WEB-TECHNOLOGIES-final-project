package it.polimi.tiw.dao;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.exceptions.LoginException;
import it.polimi.tiw.utils.ConnectionsHandler;
import jakarta.servlet.ServletContext;
import jakarta.servlet.UnavailableException;

import java.sql.*;
import java.util.Random;

public class UserDAO {

    /**
     * login verification
     * @param pwd user password
     * @param user user credentials (email and username) this obj will be saved in the session
     * @return true if the login credentials are correct
     */
    public static boolean checkCredentials(String pwd, User user, ServletContext context) throws UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        String query = "SELECT  username, userID, userPWD, userRole, userMail  FROM usersCredentials WHERE userPWD =? AND (username = ? OR userMail = ?)";
        try  {
            PreparedStatement statement = cnt.prepareStatement(query);
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getUsername());
            statement.setString(1, pwd);
            ResultSet result = statement.executeQuery();
                if (!result.isBeforeFirst()){
                    return false;
                }
                result.next();
                user.setRole(result.getInt("userRole"));
                user.setEmail(result.getString("userMail"));
                user.setUserID(result.getString("userID"));
                return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            ConnectionsHandler.releaseConnection(cnt);
        }
    }

    /**
     * verification of the saved login in cookies
     * @param userID the ID of the user randomly generated
     * @param user user credentials (email and username) this obj will be saved in the session
     * @return true if the login cookie is valid
     */
    public static boolean checkUserID(String userID, User user, ServletContext context) throws UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        String query = "SELECT  username, userID, userPWD, userRole, userMail FROM usersCredentials WHERE userID = ?";
        try (PreparedStatement statement = cnt.prepareStatement(query)) {
            statement.setString(1, userID);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) {
                    return false;
                }
                result.next();
                user.setUsername(result.getString("username"));
                user.setEmail(result.getString("userMail"));
                user.setRole(result.getInt("userRole"));
                user.setUserID(result.getString("userID"));
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
                ConnectionsHandler.releaseConnection(cnt);
        }
    }

    public static int getUserRole(String userID, ServletContext context) throws UnavailableException, LoginException {
        Connection cnt = ConnectionsHandler.takeConnection(context);
        String query = "SELECT userRole FROM usersCredentials WHERE userID = ?";
        try {
            PreparedStatement statement = cnt.prepareStatement(query);
            statement.setString(1, userID);
            ResultSet result = statement.executeQuery();

            if(!result.isBeforeFirst()) throw new LoginException("non existing user is trying to modify the DB");

            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * used from a new user to sign in
     * @param user user credentials (email and username) this obj will be saved in the session
     * @param email user mail
     * @param pwd user password
     * @param userRole the user authorization (0=guest 1(or more)=admin)
     * @return false if already exist a user with same email or username
     */
    public static boolean signInMandatoryData(User user, String email, String pwd, int userRole, ServletContext context) throws SQLException, UnavailableException {
        Connection cnt = ConnectionsHandler.takeConnection(context);

        cnt.setAutoCommit(false); // disable autocommit
        String userID;
        try {   //create userID by random number and insert all the data in the db
            userID = UserDAO.createUserID(cnt);
            UserDAO.userInsert(user,email,pwd,userRole, userID, cnt);
            cnt.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
            cnt.rollback();
            return false;
        } catch (SQLException e) {
            cnt.rollback();
            throw new RuntimeException(e);
        }finally {
            ConnectionsHandler.releaseConnection(cnt);
            cnt.setAutoCommit(true); // enable autocommit because the connection is shared
        }

        /*set up the beams*/
        user.setRole(userRole);
        user.setEmail(email);
        user.setUserID(userID);
        return true;
    }

    /**
     * used to create the random ID for the cookies
     */
    private static String createUserID(Connection cnt) throws SQLException {
        Random rand = new Random();
        int rndNumber = rand.nextInt(99999);
        String userID = "us_ID";
        userID = userID + rndNumber + "|";


        String query = "SELECT MAX(userNum) FROM usersCredentials";
        java.sql.Statement st = cnt.createStatement();
        ResultSet result = st.executeQuery(query);

        result.next();
        return userID +result.getInt(1);
    }

    /**
     * save the user in the DB
     * @param user user credentials (email and username) this obj will be saved in the session
     * @param email user mail
     * @param pwd user password
     * @param userRole the user authorization (0=guest 1(or more)=admin)
     * @param userID the ID of the user randomly generated
     */
    private static void userInsert(User user, String email, String pwd, int userRole, String userID, Connection cnt) throws SQLException {
        String query = "INSERT into usersCredentials (userNum, userID, username, userPWD, userRole, userMail) VALUES(null, ?, ?, ?, ?, ?)";
        PreparedStatement statement = cnt.prepareStatement(query);

        statement.setString(1, userID);
        statement.setString(2, user.getUsername());
        statement.setString(3, pwd);
        statement.setInt(4, userRole);
        statement.setString(5, email);
        statement.executeUpdate();
    }
}
