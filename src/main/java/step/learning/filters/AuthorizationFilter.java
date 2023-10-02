package step.learning.filters;

import com.google.inject.Singleton;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Singleton
public class AuthorizationFilter implements Filter {
    private FilterConfig _filterConfig;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        _filterConfig = filterConfig;
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req =  (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        String authHeader = req.getHeader("Authorization");
        if(authHeader != null){
            servletRequest.setAttribute("auth", "true");
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
    @Override
    public void destroy() {
        _filterConfig = null;
    }
}
