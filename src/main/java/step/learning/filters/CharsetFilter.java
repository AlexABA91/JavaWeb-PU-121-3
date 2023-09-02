package step.learning.filters;

import jdk.nashorn.api.scripting.ScriptUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipOutputStream;

public class CharsetFilter implements Filter {
    private FilterConfig _filterConfig;
    public void init(FilterConfig filterConfig) throws ServletException {
          _filterConfig = filterConfig;
    }
    public void doFilter(
            ServletRequest servletRequest,            // метод работы фильтра не HTTP - параметр
            ServletResponse servletResponse,         // но реально это HTTP данные
            FilterChain filterChain                  // Ссылка на цепочку
    ) throws IOException, ServletException {
        // По необходимости работы м req/res их нужно кастить к HTTP
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        /*
          Кодирование req/res можно установить до первого чтения/ссылки к ним. Фильтр - идеальное место для этого
        */

        // Способ передать данные про работу фильтра - атрибут запроса
           servletRequest.setAttribute("charset", StandardCharsets.UTF_8.name());

        System.out.println(
           request.getRequestURI());
        //Не забыть передать работу по цепочке
        filterChain.doFilter(servletRequest,servletResponse);
        // код записанный после цепочки будет выполниться на обратном пути
    }
    public void destroy() {
        _filterConfig = null;
    }
}

/*
* /Фильтры (сервлетные фильтры)
* Классы для сетевых задач роль которых - созданные концепции Middleware -
* создание цепочки объектов - исполнителей с возможностью вставки новых объектов в этой цепочке
* Цепочка (в обработке web-запроса)имеет прямой и обратный ход фильтры имеют возможность работать
* во всех направлениях.
* Фильтры как правело добавлять к многим маршрутам (часто - ко всем), тогда как сер влеты привязанные к одному групповому маршруту (/user/)
* Задачи фильтров - общие действия, как установление кодирования символов,
* подключение к БД, проверка авторизации, и другое.
* Особенности фильтров есть то что они не разделяются по методу запроса
*
* Описание фильтра не включает его в процесс необходима регистрация
* - или WEB.XML
* - или аннотацией
* для фильтров очень важен порядок по этому минус аннотаций по НЕ гарантированию порядка есть критический
 * */
/*
ServletContext - окружение, в котором работает сервлет снова для определения адресов как URL так и файлов

http://localhost:8080/JavaWeb_PU121/jsp?text=Hello
getContextPath() -- /JavaWeb_PU121            | Контекстный путь
getRequestURI()  -- /JavaWeb_PU121/jsp        | полный путь (без параметров)
getServletPath() -- /jsp                      | путь - база маршрута сервлета (чо что в @WebServlet("/aboutServlet"))
getPathInfo()    -- null                      | вариативная часть пути (*), если маршрут типа "/jsp/*"
getProtocol()    -- HTTP/1.1                  |
getScheme()      -- http
getRemoteHost()  -- 0:0:0:0:0:0:0:1
getQueryString() -- text=Hello


Что для файловых путей:
(new File("./")).getAbsolutePath() -- C:\Servers\apache-tomcat-8.5.93\bin\.
   ==> наши коды выполняются через веб-сервер (tomcat), соответственно
        папка "./" є рабочей папкой ехе-файла сервера

getServletContext().getRealPath("./") -- ...\repos\JavaWeb-PU121\target\JavaWeb-PU121\
        ==> показывает на папку деплою (target) - где расположен скомпилированные классы сервлета
        analog
        - _filterConfig.getServletContext().getRealPath("./")
        - request.getServletContext().getRealPath("./")

 */