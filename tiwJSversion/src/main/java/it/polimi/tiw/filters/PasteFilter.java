package it.polimi.tiw.filters;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * verify url parameters needed for paste aren't null and has significant value
 */
public class PasteFilter  implements Filter {
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {


            HttpServletRequest req = (HttpServletRequest) servletRequest;
            HttpServletResponse resp = (HttpServletResponse) servletResponse;

            String where = req.getParameter("where");
            String from = req.getParameter("from");
            String remove= req.getParameter("remove");

            if  (where == null || from == null|| remove == null){
                resp.sendRedirect(req.getServletContext().getContextPath()+ "/mainPage.html");
                return;
            }

            // pass the request along the filter chain
            filterChain.doFilter(servletRequest, servletResponse);
        }

}
