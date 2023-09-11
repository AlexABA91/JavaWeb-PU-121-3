package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.db.DbProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Singleton
public class HomeServlets extends HttpServlet { // название класса любое
    @Inject private DbProvider provider;

    @Override
    protected void doGet(                    // название метода - именно так вариации не допускаются
            HttpServletRequest request,      // request - объект, который предоставляется веб сервером
            HttpServletResponse response)    // response - то что отправлено как ответ
            throws ServletException, IOException {

        provider.getConnection();
        request.setAttribute(                 // Атрибуты - средство передачи данных во время передачи запроса
                "pageName",                   // ключ - имя атрибута (String)
                "home"                        // значение атрибута (Object)
        );

      String text =  request.getParameter(                // Параметры - query часть запроса (URL)
                "text"                       // ...&text = привет, или данные формы у doPost
        );                                   // значение атрибута (Object)

        request.setAttribute("text",text);

        request                              // делаем внутренней redirect - передаем работу к следующему обработчику - index.jsp
                .getRequestDispatcher(
                "WEB-INF/_layout.jsp")       // для того чтобы убрать прямой доступ к **.jsp его переносят в специальную папку закрытую
                .forward(request,response);   // web-inf
    }
}
/*
Servlets - специальные классы Java для сетевых задач.
Работа с servlets требует установки servlet-api
 <!-- https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api -->
 Для веб разработки чаще всего берется наследник HttpServlet.
 HttpServlet - аналог Controller в версии API

 После сложения Servlet нужно зарегистрировать (Без IoC)
 - через настройку сервера web.xml
 - с помощью аннотаций (servlet-api 3 b выше)

 Через web.xml
  "+" централизованные декларации - все в одном месте
      гарантированный порядок декларации

  "-" больше конструкции
  <servlet>
    <servlet-name>Home</servlet-name>
    <servlet-class>step.learning.servlets.HomeServlets</servlet-class>
  </servlet>
  <servlet-mapping>
    <servlet-name>Home</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>

*/