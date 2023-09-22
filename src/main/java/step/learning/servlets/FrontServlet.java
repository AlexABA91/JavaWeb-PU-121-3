package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.db.dao.WebTokenDao;
import step.learning.db.dto.User;
import step.learning.db.dto.WebToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Демонстрация работы с Frontend  (SPA)*/
@Singleton
public class FrontServlet extends HttpServlet {
    @Inject
    private WebTokenDao webTokenDao;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String authHeader = req.getHeader("Authorization");
        if(authHeader == null){
            // гостевой режим
            resp.getWriter().print("Guest mode");
            return;
        }
        String pattern = "Bearer (.+)$";
        // Create a Pattern object
         Pattern regex = Pattern.compile(pattern);
        // Now create matcher object.
         Matcher matches = regex.matcher(authHeader);
        if(matches.find()){
            String token = matches.group(1);
            // декодируем с base64 в объект
            WebToken webToken;
            try {
                webToken= new WebToken(token);
            } catch (ParseException ignored ) {
                resp.getWriter().print("Unpassable token " + token);
                return;
            }
            // проверяем путем поиска пользователя
            User user = webTokenDao.getSubject(webToken);

            if(user == null) {
                resp.getWriter().print("Invalid token " + token);
                return;
            }
            resp.getWriter().print("Auth mode "+ user.getFirstName());
            return;
        }
        resp.getWriter().print("invalid authorization schema");
    }
}
