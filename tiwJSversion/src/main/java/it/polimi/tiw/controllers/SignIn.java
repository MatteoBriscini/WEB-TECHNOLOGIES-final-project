package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.exceptions.StringValidatorException;
import it.polimi.tiw.utils.StringValidator;
import it.polimi.tiw.utils.staticClasses.ExceptionParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/signIn")

public class SignIn extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user;

        String username = req.getParameter("user");
        String email = req.getParameter("email");
        String pwd = req.getParameter("pwd");
        String pwd2 = req.getParameter("pwd2");

        if(username.isEmpty() || pwd.isEmpty() || email.isEmpty() || pwd2.isEmpty()){  //the user has to fill the all the text inputs
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Credentials must be not null");
            return;
        }

        try {   //validate the inserted strings with regular pattern
            StringValidator.emailCheck(email);
            StringValidator.usernameCheck(username);
            StringValidator.pwdCheck(pwd);
            StringValidator.pwdCompare(pwd,pwd2);
        } catch (StringValidatorException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(e.getMessage());
            return;
        }

        user = new User(username);
        try {
            if(UserDAO.signInMandatoryData(user, email, pwd, 1, getServletContext())){ //save the new user in the DB
                req.getSession().setAttribute("user", user);    //save the login data in the session attributes
                resp.setStatus(HttpServletResponse.SC_OK);
            }else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().println("username or email already used");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(ExceptionParser.parse(e));
        }
    }
}
