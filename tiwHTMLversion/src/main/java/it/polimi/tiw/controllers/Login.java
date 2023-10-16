package it.polimi.tiw.controllers;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.dao.UserDAO;

import it.polimi.tiw.utils.staticClasses.Links;
import it.polimi.tiw.utils.thymeleaf.PrintData;
import it.polimi.tiw.utils.thymeleaf.ThymeleafSupport;
import jakarta.servlet.ServletException;
import jakarta.servlet.UnavailableException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;


import java.io.IOException;
import java.io.Serial;
@WebServlet("/login")
public class Login extends HttpServlet {
    private TemplateEngine templateEngine;

    @Serial
    private static final long serialVersionUID = 1L;

    public void init() {
        /*init thymeleaf*/
        this.templateEngine = (TemplateEngine) getServletContext().getAttribute(ThymeleafSupport.TEMPLATE_ENGINE_ATTR);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkCookie(req,resp);

        IWebExchange webExchange = JakartaServletWebApplication.buildApplication(getServletContext()).buildExchange(req, resp);
        final WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("regHref", "/account");
        templateEngine.process(Links.index, ctx, resp.getWriter());
    }



    /**
     * check user credentials to confirm the login
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user;

        String username = req.getParameter("user");
        String pwd = req.getParameter("pwd");
        String saveLogin = req.getParameter("saveLogin");

        if(username.isEmpty() || pwd.isEmpty()){  //the user has to fill the all the text inputs
            PrintData.errorMsg("please fill all the fields", req, resp, templateEngine, "index.html", getServletContext());
            return;
        }
        user = new User(req.getParameter("user"));

        if(UserDAO.checkCredentials(pwd, user, getServletContext())){   //check the user credentials with the DB
            req.getSession().setAttribute("user", user);    //save the login in the sessions attributes

            if(saveLogin!=null){    //set cookies, and save the login
                Cookie c = new Cookie("username", user.getUserID());
                c.setMaxAge(86400);
                resp.addCookie(c);
            }

            resp.sendRedirect(getServletContext().getContextPath() + Links.mainPageServlet);
        }else{
            PrintData.errorMsg("username or pwd are incorrect!", req, resp, templateEngine, "index.html", getServletContext());
        }
    }

    /**
     * the login is saved in cookies, this method is verified if the user has saved the login previously.
     * if the user is already logged the user will be redirect to the main page
     */
    private void checkCookie(HttpServletRequest req, HttpServletResponse resp) throws IOException, UnavailableException {
        Cookie[] cookies = req.getCookies();
        if(cookies!=null) {for (Cookie c : cookies) {if(c.getName().equals("username")) {
            User user = new User();
            if (UserDAO.checkUserID(c.getValue(), user, getServletContext())) { //check if the cookies is valid with the DB
                req.getSession().setAttribute("user", user);    //set the user session attribute to confirm the login, and for future use.
                resp.sendRedirect(getServletContext().getContextPath() + "/mainPage");
                return;
            }
        }}}
    }
}
