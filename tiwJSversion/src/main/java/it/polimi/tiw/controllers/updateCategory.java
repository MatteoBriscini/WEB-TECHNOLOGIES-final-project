package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
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

@WebServlet("/updateCatName")
public class updateCategory extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int code = Integer.parseInt(req.getParameter("code"));
        String newName = req.getParameter("newName");

        User user = (User) req.getSession(true).getAttribute("user");

        try {
            StringValidator.categoryNameSpecialCharactersFilter(newName);
            CategoriesDAO.updateCategories(code,newName, user.getUserID(), getServletContext());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println(ExceptionParser.parse(e));
        } catch (StringValidatorException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("pls insert a valid name!");
            return;
        }
    }
}
