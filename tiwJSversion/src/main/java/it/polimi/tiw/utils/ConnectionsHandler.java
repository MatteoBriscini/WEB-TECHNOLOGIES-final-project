package it.polimi.tiw.utils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.UnavailableException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * this class implements a connection handler to avoid creating a new connection for each operation with the DB
 * in the DB the wait timeout for each connection is set with the value of one hour, a new connection is created only if there is no connection available in the connections list
 * when a DAO has completed all the operation instead of destroy the DB connection it will save that in the connections list for future uses
 */
public class ConnectionsHandler {
    private final static ArrayList<Connection> connections = new ArrayList<>(); //the list of all the created connection

    /**
     * this method make possible at all the DAO to retrieve the first available connection with the DB
     * @return the first available connection with the DB
     * @throws UnavailableException if the DB can't release new connection for some reason
     */
    public synchronized static Connection takeConnection(ServletContext context) throws UnavailableException {
        Connection connection;

        try {
            while (!connections.isEmpty() && connections.get(0).isValid(3600))connections.remove(0);
            if(!connections.isEmpty()){//if the selected connection is no more active
                connection = connections.get(0);
                connections.remove(0);
            } else {//if there is no more active connection available
                connection = ConnectionsHandler.dbInit(context);
            }
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Can't load database driver");
        } catch (SQLException e) {
            throw new UnavailableException("Couldn't get db connection");
        }
        return connection;
    }

    /**
     * when a DAO has completed all the operation has to give back the used DB connection
     * @param cnt the used DB connection
     */
    public synchronized static void releaseConnection(Connection cnt){
        if(cnt!=null)connections.add(cnt);
    }

    /**
     * create a new connection with the DB
     * @return the just created DB connection
     */
    private static Connection dbInit(ServletContext context) throws ClassNotFoundException, SQLException {
            String driver = context.getInitParameter("dbDriver");
            String url = context.getInitParameter("dbUrl");
            String user = context.getInitParameter("dbUser");
            String password = context.getInitParameter("dbPassword");
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
    }
}
