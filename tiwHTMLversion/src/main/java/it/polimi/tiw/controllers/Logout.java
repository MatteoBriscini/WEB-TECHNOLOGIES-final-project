package it.polimi.tiw.controllers;

import it.polimi.tiw.utils.staticClasses.Links;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@WebServlet("/Logout")
public class Logout extends HttpServlet {
    /**
     * implements the logout button
     * this method remove the user attributes from the session and the username value from cookies
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().removeAttribute("user");

        Cookie c = new Cookie("username","");
        c.setMaxAge(0);
        resp.addCookie(c);

        resp.sendRedirect(getServletContext().getContextPath()+ "/" + Links.index);
    }
}
