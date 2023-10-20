package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.beams.Category;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.utils.staticClasses.ExceptionParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/searchCategory")
public class SearchCategory extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String string = req.getParameter("search"); //searched values (can be the category ID or the category name)
        ArrayList<String> search = new ArrayList<>(Arrays.asList(string.split(","))); //user can search multiple parameters separating them with ,
        try {
            ArrayList<Category> categories = CategoriesDAO.searchCategory(search, getServletContext());//try to search the user input in the DB

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(new Gson().toJsonTree(categories).getAsJsonArray());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(ExceptionParser.parse(e));
        }
    }
}
