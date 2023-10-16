package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import it.polimi.tiw.beams.Category;
import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.exceptions.CategoryDBException;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/taxonomyModification")
public class TaxonomyModification extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int from = Integer.parseInt(req.getParameter("from")); //represent the category ID on which the action is performed
        int where = Integer.parseInt(req.getParameter("where"));    //TODO gestire non int parameters
        boolean remove = Boolean.parseBoolean(req.getParameter("remove"));

        try {
            JsonArray categories = new Gson().toJsonTree(CategoriesDAO.getModifiedTaxonomy(from,where,remove,getServletContext())).getAsJsonArray();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(categories);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int from = Integer.parseInt(req.getParameter("from"));  //the ID of the category on the top of the selected tree
        int where = Integer.parseInt(req.getParameter("where"));    //the ID of the category under which the selected tree have to be pasted
        boolean remove = Boolean.parseBoolean(req.getParameter("remove"));  //if this value is set to true

        User user = (User) req.getSession().getAttribute("user");

        try {
            List<Category> categories = CategoriesDAO.pasteSubTree(from,where, user.getUserID(), remove, getServletContext());

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(new Gson().toJsonTree(categories).getAsJsonArray());

        } catch (SQLException | UnavailableException | CategoryDBException e) {
            throw new RuntimeException(e);
        }

    }
}
