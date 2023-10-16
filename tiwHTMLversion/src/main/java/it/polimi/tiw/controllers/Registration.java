package it.polimi.tiw.controllers;


import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.exceptions.StringValidatorException;
import it.polimi.tiw.utils.staticClasses.Links;
import it.polimi.tiw.utils.thymeleaf.PrintData;
import it.polimi.tiw.utils.StringValidator;
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


@WebServlet("/account")
public class Registration extends HttpServlet {
    private TemplateEngine templateEngine;

    public void init() {
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafSupport.TEMPLATE_ENGINE_ATTR);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        final WebContext ctx = new WebContext(webExchange);
        templateEngine.process(Links.regPage, ctx, resp.getWriter());
    }
    /**
     * implements the registration form
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user;

        String s = getServletContext().getContextPath();
        String username = req.getParameter("user");
        String pwd = req.getParameter("pwd");
        String pwd2 = req.getParameter("pwd2");
        String mail = req.getParameter("email");
        if(username.isEmpty() || pwd.isEmpty() || mail.isEmpty()){  //all mandatory data need to be filled
            PrintData.errorMsg("please fill all the mandatory fields", req, resp, templateEngine, "/WEB-INF/account.html", getServletContext());
            return;
        }

        try {   //validate the inserted strings with regular pattern
            StringValidator.emailCheck(mail);
            StringValidator.usernameCheck(username);
            StringValidator.pwdCheck(pwd);
            StringValidator.pwdCompare(pwd,pwd2);
        } catch (StringValidatorException e) {
            PrintData.errorMsg(e.getMessage(), req, resp, templateEngine, "/WEB-INF/account.html", getServletContext());
            return;
        }

        user = new User(username);
        try {
            if(UserDAO.signInMandatoryData(user, mail, pwd, 1, getServletContext())){ //save the new user in the DB
                req.getSession().setAttribute("user", user);    //save the login data in the session attributes
                resp.sendRedirect(s+ "/mainPage");
            }else {
                PrintData.errorMsg("username or email already used", req, resp, templateEngine, "/WEB-INF/account.html", getServletContext());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
