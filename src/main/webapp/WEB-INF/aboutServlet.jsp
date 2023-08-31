<%@ page contentType="text/html;charset=UTF-8" %>
<h1>Про Servlet</h1>
<p>Servlets - специальные классы Java для сетевых задач.
    Работа с servlets требует установки servlet-api
    <!-- https://mvnrepository.com/artifact/javax.servlet/javax.servlet-api -->
    Для веб разработки чаще всего берется наследник HttpServlet.
    HttpServlet - аналог Controller в версии API
</p>
<p>После сложения Servlet нужно зарегистрировать (Без IoC)
    - через настройку сервера web.xml
    - с помощью аннотаций (servlet-api 3 b выше)
</p>
<p>Через web.xml
    "+" централизованные декларации - все в одном месте
    гарантированный порядок декларации

    "-" больше конструкции
</p>
<pre>
   &lt;servlet&gt;
     &lt;servlet-name&gt;Home&lt;/servlet-name&gt;
        &lt;servlet-class&gt;step.learning.servlets.HomeServlets&lt;/servlet-class&gt;
     &lt;/servlet&gt;
     &lt;servlet-mapping&gt;
        &lt;servlet-name>Home&lt;/servlet-name&gt;
        &lt;url-pattern&gt;/&lt;/url-pattern&gt;
   &lt;/servlet-mapping&gt;
</pre>