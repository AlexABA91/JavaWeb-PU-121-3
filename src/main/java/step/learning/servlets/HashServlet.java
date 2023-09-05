package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.hesh.HashService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class HashServlet extends HttpServlet {
    @Inject
    public HashServlet(HashService hashService) {
        this.hashServlet = hashService;
    }

    private final HashService hashServlet;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("hashString" ,this.hashServlet.hash("123") );
        req.setAttribute("pageName","hash");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req,resp);
    }
}
