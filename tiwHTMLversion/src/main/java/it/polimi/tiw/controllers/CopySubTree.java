package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.CategoriesDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.exceptions.LoginException;
import it.polimi.tiw.utils.staticClasses.Links;
import it.polimi.tiw.utils.thymeleaf.PrintData;
import it.polimi.tiw.utils.thymeleaf.ThymeleafSupport;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
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

@WebServlet("/copyCat")
public class CopySubTree extends HttpServlet {
    private TemplateEngine templateEngine;
    public void init() throws ServletException {
        /*init thymeleaf*/
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafSupport.TEMPLATE_ENGINE_ATTR);
    }

    /**
     * implements the copy, cut or delete action
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        this.pageContentAdder(req,resp,ctx);
    }
    /**
     * print the action result in the html page
     */
    private void pageContentAdder(HttpServletRequest req, HttpServletResponse resp,WebContext ctx) throws IOException {
        String from = req.getParameter("from"); //represent the category ID on which the action is performed
        String action = req.getParameter("action");  //used to distinguish the desired action

        ctx.setVariable("fromCode",Integer.parseInt(from)); //the category ID need to be saved for future paste action

        User user = (User) req.getSession().getAttribute("user");
        PrintData.fillUserInfo(user, ctx);
        PrintData.showAdminMenu(req,ctx);

        switch (action){
            case "copy":
                ctx.setVariable("remove",false);        //during the paste action the selected categories does not need to be removed
                ctx.setVariable("copy", Boolean.FALSE);     //hide the copy, cut and delete button from the menu
                ctx.setVariable("paste", Boolean.TRUE);     //show the paste button from the menu
                break;
            case "cut":
                ctx.setVariable("remove",true);         //during the paste action the selected categories is needed to be removed
                ctx.setVariable("copy", Boolean.FALSE);
                ctx.setVariable("paste", Boolean.TRUE);
                break;
            case "delete":
                try {
                    if(UserDAO.getUserRole(user.getUserID(), getServletContext())>=1) {
                        ctx.setVariable("copy", Boolean.TRUE);
                        ctx.setVariable("paste", Boolean.FALSE);

                        CategoriesDAO.removeSubTree(Integer.parseInt(from), getServletContext()); //perform the delete action
                    }
                    else throw new RuntimeException("not authorized user has try to remove category");
                } catch (UnavailableException | LoginException | SQLException e) {
                    throw new RuntimeException(e);
                }


                break;
        }
        PrintData.fillTable(ctx, Integer.parseInt(from), getServletContext());

        templateEngine.process(Links.mainPage, ctx, resp.getWriter());
    }
}
