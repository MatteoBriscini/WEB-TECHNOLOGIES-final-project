package it.polimi.tiw.filters;

import it.polimi.tiw.beams.User;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * filter for admin access
 * used in pages with restrict access
 */
public class AdminFirstLevelFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        User user = (User) req.getSession().getAttribute("user");

        if(user == null || user.getRole()<1){
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("user isn't authorized for this operation");
            return;
        }

        // pass the request along the filter chain
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
