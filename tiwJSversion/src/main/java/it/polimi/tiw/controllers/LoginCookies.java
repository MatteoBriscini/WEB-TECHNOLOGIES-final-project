package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet("/loginCookies")
public class LoginCookies extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userID = req.getParameter("userID");
        User user = new User();
        if (UserDAO.checkUserID(userID, user, getServletContext())) {
            req.getSession().setAttribute("user", user);    //save the login in the sessions attributes
            resp.setStatus(HttpServletResponse.SC_OK);
        }else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}