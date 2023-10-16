package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.exceptions.CategoryDBException;
import it.polimi.tiw.utils.staticClasses.Links;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
@WebServlet("/pasteCat")
public class PasteSubTree extends HttpServlet {

    /**
     * implements the paste button
     */
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.pageContentAdder(req,resp);
    }
    /**
     * print the action result in the html page and act on the DB
     */
    private void pageContentAdder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int from = Integer.parseInt(req.getParameter("from"));  //the ID of the category on the top of the selected tree
        int where = Integer.parseInt(req.getParameter("where"));    //the ID of the category under which the selected tree have to be pasted
        boolean remove = Boolean.parseBoolean(req.getParameter("remove"));  //if this value is set to true

        User user = (User) req.getSession().getAttribute("user");

        try {
            CategoriesDAO.pasteSubTree(from,where, user.getUserID(), remove, getServletContext());  //
        } catch (SQLException | UnavailableException | CategoryDBException e) {
            throw new RuntimeException(e);
        }

        resp.sendRedirect(getServletContext().getContextPath()+ Links.mainPageServlet);
    }
}