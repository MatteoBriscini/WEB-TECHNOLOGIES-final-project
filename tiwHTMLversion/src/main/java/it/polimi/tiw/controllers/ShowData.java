package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.User;
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

@WebServlet("/mainPage")
public class ShowData extends HttpServlet {
    private TemplateEngine templateEngine;
    public void init() throws ServletException {
        /*init thymeleaf*/
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafSupport.TEMPLATE_ENGINE_ATTR);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        this.pageContentAdder(req,resp,new WebContext(webExchange));
    }

    /**
     * print the action result in the html page
     */
    private void pageContentAdder(HttpServletRequest req, HttpServletResponse resp,WebContext ctx) throws IOException {

        PrintData.fillTable(ctx, getServletContext());  //add all the categories in the list
        PrintData.showAdminMenu(req,ctx);   //if the user has a role of admin the menu need to be showed
        PrintData.fillUserInfo((User) req.getSession().getAttribute("user"), ctx);

        templateEngine.process(Links.mainPage, ctx, resp.getWriter());
    }
}
