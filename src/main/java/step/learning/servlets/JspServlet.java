package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.hesh.HashService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
//@WebServlet("/jsp") // замена декларации в web.xml
public class JspServlet extends HttpServlet {
    private final Logger logger;
    private final HashService haskService;
    @Inject
    public JspServlet(Logger logger, HashService hashService){
        this.logger = logger;
        this.haskService = hashService;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.log(Level.INFO,"INFO from JspServlet " + haskService.hash("123"));
        //logger.log(Level.WARNING,"Warning from JspServlet");
        req.setAttribute("pageName","jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req,resp);
    }
}
