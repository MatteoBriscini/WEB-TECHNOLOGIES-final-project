package it.polimi.tiw.controllers;

import it.polimi.tiw.dao.CategoriesDAO;
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

        try {
            CategoriesDAO.updateCategories(code,newName,getServletContext());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
