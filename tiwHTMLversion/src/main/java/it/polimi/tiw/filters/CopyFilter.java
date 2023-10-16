package it.polimi.tiw.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

/**
 * verify url parameters needed for copy, cut and delete aren't null and has significant value
 */
public class CopyFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        String from = req.getParameter("from");
        String action = req.getParameter("action");

        if  (from == null || (!Objects.equals(action, "copy") && !Objects.equals(action, "cut") &&!Objects.equals(action, "delete"))){
            resp.sendRedirect(req.getServletContext().getContextPath()+ "/mainPage");
            return;
        }

        // pass the request along the filter chain
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
