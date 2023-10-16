package it.polimi.tiw.controllers;

import com.google.gson.Gson;
import it.polimi.tiw.beams.Category;
import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.exceptions.LoginException;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/removeCat")
public class RemoveCategory extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int code = Integer.parseInt(req.getParameter("code"));
        User user = (User) req.getSession().getAttribute("user");

        try {
            if(UserDAO.getUserRole(user.getUserID(), getServletContext())>=1) {
                List<Category> categories = CategoriesDAO.removeSubTree(code, getServletContext());

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().println(new Gson().toJsonTree(categories).getAsJsonArray());

            }else {throw new RuntimeException("not authorized user has try to remove category");}
        } catch (UnavailableException | LoginException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
