package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.db.dao.UserDao;
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
    private KdfService kdfService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //userDao.install();

            User newUser = new User();
            newUser.setEmail("alex@gmail.com");
            newUser.setLogin("Alex");
            newUser.setSalt("514511ee");
            String defaultPassword ="admin123";
            String passwordDK = kdfService.getDerivedKye(defaultPassword,"514511ee");
            newUser.setPasswordDk(passwordDK);
            userDao.addUser(newUser);
            resp.getWriter().print("User add");
            //resp.getWriter().print("Install Ok");
        }catch (RuntimeException ex){
            resp.getWriter().print("Install Error. Look at logs");
        }
    }
}
