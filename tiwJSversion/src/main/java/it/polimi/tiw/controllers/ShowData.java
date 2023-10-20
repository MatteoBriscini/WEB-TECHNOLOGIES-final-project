package it.polimi.tiw.controllers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.tiw.beams.Category;
import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.utils.staticClasses.ExceptionParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/mainPage")
public class ShowData extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            JsonArray categories = new Gson().toJsonTree(CategoriesDAO.getCategories(getServletContext())).getAsJsonArray();
            JsonObject user = new Gson().toJsonTree((User) req.getSession().getAttribute("user")).getAsJsonObject();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("userData", user);
            jsonObject.add("categories", categories);
            //System.out.println(user);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().println(jsonObject);
        } catch (SQLException | UnavailableException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(ExceptionParser.parse(e));
        }
    }
}
