package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.db.dao.UserDao;
import step.learning.db.dao.WebTokenDao;
import step.learning.db.dto.User;
import step.learning.services.kdf.KdfService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class InstallServlet extends HttpServlet {
    @Inject
    private UserDao userDao;
    @Inject
    private WebTokenDao webTokenDao;
    @Inject
    private KdfService kdfService;



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //userDao.install();
            webTokenDao.Install();
            //resp.getWriter().print("Install Ok");
        }catch (RuntimeException ex){
            resp.getWriter().print("Install Error. Look at logs");
        }
    }
}
