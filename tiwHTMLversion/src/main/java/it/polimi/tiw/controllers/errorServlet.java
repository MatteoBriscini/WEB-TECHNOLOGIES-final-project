package it.polimi.tiw.controllers;

import it.polimi.tiw.utils.staticClasses.Links;
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

import static jakarta.servlet.RequestDispatcher.*;

@WebServlet("/error")
public class errorServlet extends HttpServlet {
    private TemplateEngine templateEngine;
    private static final long serialVersionUID = 1L;

    public void init() throws ServletException {
        /*init thymeleaf*/
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafSupport.TEMPLATE_ENGINE_ATTR);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.errorPage(req,resp);
    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        this.errorPage(req,resp);
    }

    private void errorPage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        final WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("statusCode", "HTTP Status " + req.getAttribute(ERROR_STATUS_CODE));
        if(req.getAttribute(ERROR_MESSAGE)!=null && !req.getAttribute(ERROR_MESSAGE).equals(""))ctx.setVariable("errorMessage", req.getAttribute(ERROR_MESSAGE));
        if(req.getAttribute(ERROR_EXCEPTION)!=null){
            String[] exception = req.getAttribute(ERROR_EXCEPTION).toString().split(":");//select only the exception message
            ctx.setVariable("errorException", exception[exception.length-1]);
        }
        templateEngine.process(Links.errorPage, ctx, resp.getWriter());
    }
}
