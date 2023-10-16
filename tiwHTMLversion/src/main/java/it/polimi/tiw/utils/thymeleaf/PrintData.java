package it.polimi.tiw.utils.thymeleaf;

import it.polimi.tiw.beams.Category;
import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrintData {
    /**
     * called if the user didn't fill the add new category form in the correct way
     * @param msg the error message printed to the user
     */
    public static void errorMsg(String msg, HttpServletRequest req, HttpServletResponse resp, TemplateEngine templateEngine, String pageIndex, ServletContext sc) throws IOException {
        String path = pageIndex;

        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(sc).buildExchange(req, resp);
        final WebContext ctx = new WebContext(webExchange);

        ctx.setVariable("errMsg", msg);
        templateEngine.process(path, ctx, resp.getWriter());
    }

    /**
     * take all the categories from the DB and fill the taxonomy in the UI
     */
    public static void fillTable(WebContext ctx, ServletContext context){
        List<Category> categories = new ArrayList<>();
        try {
            categories = CategoriesDAO.getCategories(context);
        } catch (SQLException | UnavailableException e) {
            throw new RuntimeException(e);
        }
        ctx.setVariable("categories", categories);
    }

    /**
     * take all the categories from the DB and fill the taxonomy in the UI
     * @param fatherCode optional parameter used to fill the taxonomy where there are selected categories
     */
    public static void fillTable(WebContext ctx, int fatherCode, ServletContext context){
        List<Category> categories;
        try {
            categories = CategoriesDAO.getCategories(fatherCode, context);
        } catch (SQLException | UnavailableException e) {
            throw new RuntimeException(e);
        }
        ctx.setVariable("categories", categories);
    }

    /**
     * take all the user info from the DB and add it to the UI
     */
    public static void fillUserInfo(User user, WebContext ctx){
        ctx.setVariable("username", "USERNAME:  " + user.getUsername());
        ctx.setVariable("email", "EMAIL:    "+ user.getEmail());
        ctx.setVariable("userRole", "ROLE:  " + ((user.getRole()>=1)?"admin":"guest"));
    }

    /**
     * if the logged user has the authorization the admin menu will be added to the UI
     */
    public static void showAdminMenu(HttpServletRequest req, WebContext ctx){
        User user = (User) req.getSession(true).getAttribute("user");
        if(user.getRole()>=1) {
            ctx.setVariable("roleCondition", Boolean.TRUE);
            ctx.setVariable("copy", Boolean.TRUE);
        }else {
            ctx.setVariable("roleCondition", Boolean.FALSE);
        }
    }
}
