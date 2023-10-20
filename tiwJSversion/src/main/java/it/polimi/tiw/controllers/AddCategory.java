package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.tiw.beams.Category;
import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.exceptions.CategoryDBException;
import it.polimi.tiw.utils.staticClasses.ExceptionParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/addCategory")
public class AddCategory extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int fatherCode = 0;

        if(!req.getParameter("fatherCode").isEmpty()) {
            try{ fatherCode = Integer.parseInt(req.getParameter("fatherCode"));}
            catch (NumberFormatException e){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().println("pls insert a valid father code!");
                return;
            }
        }

        String name = req.getParameter("categoryName");
        if(name.isEmpty()){ //name is not optional
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("pls insert a valid name!");
            return;
        }

        User user = (User) req.getSession(true).getAttribute("user");

        try {
            List<Category> categories;
            if(fatherCode==0) categories = CategoriesDAO.addNewHead(name, user.getUserID(), getServletContext());
            else categories = CategoriesDAO.addCategory(fatherCode, name, user.getUserID(), getServletContext());

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(new Gson().toJsonTree(categories).getAsJsonArray());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (CategoryDBException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(e.getMessage());
        }
    }
}
