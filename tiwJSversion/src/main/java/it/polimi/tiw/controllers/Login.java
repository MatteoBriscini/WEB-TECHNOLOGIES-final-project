package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.staticClasses.Links;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet("/login")
public class Login extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("user");
        String pwd = req.getParameter("pwd");
        String saveLogin = req.getParameter("saveLogin");

        if(username.isEmpty() || pwd.isEmpty()){  //the user has to fill the all the text inputs
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Credentials must be not null");
            return;
        }

        User user = new User(username);
        if(UserDAO.checkCredentials(pwd, user, getServletContext())) {   //check the user credentials with the DB
            req.getSession().setAttribute("user", user);    //save the login in the sessions attributes

            if(saveLogin!=null){    //set cookies, and save the login
                Cookie c = new Cookie("username", user.getUserID());
                c.setMaxAge(86400);
                resp.addCookie(c);
            }

            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("Incorrect credentials");
        }
    }

}
