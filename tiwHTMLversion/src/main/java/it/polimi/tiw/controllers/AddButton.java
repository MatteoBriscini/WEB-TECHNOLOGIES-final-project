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

@WebServlet("/addButton")
public class AddButton extends HttpServlet {
    private TemplateEngine templateEngine;
    public void init() {
        /*init thymeleaf*/
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafSupport.TEMPLATE_ENGINE_ATTR);
    }

    /**
     * implements the + button action and fill the father code input in the "add new category form"
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int fatherCode = 0;
        String tmp = req.getParameter("fatherCode");
        if(!tmp.isEmpty())fatherCode= Integer.parseInt(tmp);

        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        PrintData.fillTable(ctx, getServletContext());
        PrintData.showAdminMenu(req,ctx);
        PrintData.fillUserInfo((User) req.getSession(true).getAttribute("user"),ctx);

        ctx.setVariable("fatherCode", fatherCode);

        templateEngine.process(Links.mainPage, ctx, resp.getWriter());
    }
}
