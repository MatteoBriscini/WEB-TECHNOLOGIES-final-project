package it.polimi.tiw.filters;

import it.polimi.tiw.beams.User;
import it.polimi.tiw.utils.staticClasses.Links;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        User user = (User) req.getSession().getAttribute("user");
        if(user == null){
            resp.setStatus(403);
            resp.setHeader("Location", req.getServletContext().getContextPath()+ "/" + Links.index);
            return;
        }

        // pass the request along the filter chain
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
