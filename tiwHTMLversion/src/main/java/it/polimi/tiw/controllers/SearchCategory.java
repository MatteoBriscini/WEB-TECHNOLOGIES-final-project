package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.Category;
import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.utils.staticClasses.Links;
import it.polimi.tiw.utils.thymeleaf.PrintData;
import it.polimi.tiw.utils.thymeleaf.ThymeleafSupport;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/searchCategory")
public class SearchCategory extends HttpServlet {
    private TemplateEngine templateEngine;
    public void init() throws ServletException {
        /*init thymeleaf*/
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafSupport.TEMPLATE_ENGINE_ATTR);
    }

    /**
     * implements the search bar
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String string = req.getParameter("search"); //searched values (can be the category ID or the category name)
        ArrayList<String> search = new ArrayList<>(Arrays.asList(string.split(","))); //user can search multiple parameters separating them with ,
        try {
            ArrayList<Category> categories = CategoriesDAO.searchCategory(search, getServletContext());//try to search the user input in the DB

            IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
            WebContext ctx = new WebContext(webExchange);
            ctx.setVariable("categories", categories);
            PrintData.fillUserInfo((User) req.getSession().getAttribute("user"), ctx);
            PrintData.showAdminMenu(req,ctx);

            templateEngine.process(Links.mainPage, ctx, resp.getWriter());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
