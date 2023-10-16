package it.polimi.tiw.utils.thymeleaf;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

@WebListener
public class ThymeleafSupport implements ServletContextListener {
    public static final String TEMPLATE_ENGINE_ATTR = "com.huongdanjava.thymeleaf.TemplateEngineInstance";
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(sce.getServletContext());
        ITemplateEngine templateEngine = templateEngine(application);
        sce.getServletContext().setAttribute(TEMPLATE_ENGINE_ATTR, templateEngine);
    }

    private ITemplateEngine templateEngine(IWebApplication application) {
        TemplateEngine templateEngine = new TemplateEngine();
        WebApplicationTemplateResolver templateResolver = templateResolver(application);
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

    private WebApplicationTemplateResolver templateResolver(IWebApplication application) {
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3600000L);
        templateResolver.setCacheable(true);
        return templateResolver;
    }
}