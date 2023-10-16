package it.polimi.tiw.controllers;


import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.exceptions.CategoryDBException;
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

@WebServlet("/addCategory")
public class AddCategory extends HttpServlet {
    private TemplateEngine templateEngine;
    public void init() throws ServletException {
        /*init thymeleaf*/
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafSupport.TEMPLATE_ENGINE_ATTR);
    }

    /**
     * save the new added category in the DB
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        /*
        the father code is an optional parameter in the new category form,
        if the input is empty the parameter is considered as zero and the category is added on the top of the list
        */
        int fatherCode = 0;
        if(!req.getParameter("fatherCode").isEmpty()) {
            try{ fatherCode = Integer.parseInt(req.getParameter("fatherCode"));}
            catch (NumberFormatException e){this.errorCode(req,resp,"pls insert a valid father code!");}
        }
        String name = req.getParameter("name"); //the chosen name fot the new category

        if(name.isEmpty()){ //name is not optional
            this.errorCode(req,resp,"pls insert a valid name!");
            return;
        }

        /*
         when a new category is added the ID of the creator is saved with the category
         a trigger in the DB check if the user has the authorization to modify the data
         */
        User user = (User) req.getSession(true).getAttribute("user");

        try {
            if(fatherCode==0) CategoriesDAO.addNewHead(name, user.getUserID(), getServletContext());
            else CategoriesDAO.addCategory(fatherCode, name, user.getUserID(), getServletContext());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (CategoryDBException e) {
            this.errorCode(req,resp,e.getMessage());
        }finally {
            resp.sendRedirect(getServletContext().getContextPath() + "/mainPage");
        }

    }

    /**
     * called if the user didn't fill the add new category form in the correct way
     * @param errMsg the error message printed to the user
     */
    private void errorCode(HttpServletRequest req, HttpServletResponse resp, String errMsg) throws IOException {
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("errMsg", errMsg);
        this.pageContentAdder(req,resp,ctx);
    }

    /**
     * print the action result in the html page
     */
    private void pageContentAdder(HttpServletRequest req, HttpServletResponse resp,WebContext ctx) throws IOException {
        ctx.setVariable("copy", Boolean.TRUE);
        PrintData.fillTable(ctx, getServletContext());
        PrintData.showAdminMenu(req,ctx);

        PrintData.fillUserInfo((User) req.getSession().getAttribute("user"),ctx);

        templateEngine.process(Links.mainPage, ctx, resp.getWriter());
    }
}
