package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.hesh.HashService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Singleton
public class HashServlet extends HttpServlet {
    @Inject
    public HashServlet(HashService hashService) {
        this.hashServlet = hashService;
    }

    private final HashService hashServlet;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String inputText =  req.getParameter("input_text");
        if(inputText == null){
            resp.setStatus(406);
            resp.getWriter().println("Required input_text parameter");
        }

        String result
            = String.format("hash('%s') = %s",inputText,this.hashServlet.hash(inputText)) ;


        if("download".equals(req.getParameter("mode"))){
            String hashResult = req.getParameter("textResult");
            String text =  hashResult.substring(hashResult.indexOf("'")+1,hashResult.lastIndexOf("'"));

            String fileName = text.length() <= 8 ?text : text.substring(0,8);
            //нажата кнопка "download"
            // для передачи ответа как файла необходимо установить заголовки
            resp.setHeader("Content-Type", "application/octet-stream");
            // за типом содержимого или обобщенного типа "application/octet-stream"
            // это гарантирует что браузер не будет пробовать открыть этот файл, а перейдет в скачивание

            // не обязательно - добавляем ведомости про название файла (в котором он будет сохраниться в браузере)
            resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s.txt\"",fileName) );
            // содержимое записывается в файл
            resp.getWriter().print(hashResult);
            return;
        }
        HttpSession session =  req.getSession();
        session.setAttribute("hashString" , result);
        req.setAttribute("pageName","hash");
        resp.sendRedirect(req.getRequestURI());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       HttpSession session = req.getSession();
        String result = (String)session.getAttribute("hashString");
        if(result != null) {
            req.setAttribute("hashString", result);
            session.removeAttribute("hashString");
        }
        req.setAttribute("pageName", "hash");
        req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req, resp);
    }
}
/*
Работа с формами и передача данных
 Параметры из запроса достаются методом erq.getParameter(name)
   название параметра как в поле Form(input name="input_text"...)
   если нужен полный список параметров то методы
   req.getParameterNames() - имена параметров
   req.getParameterMar() - имена и значения, этот метод собирает в массив
     значения параметров с одинаковыми именами

1. Get
   методом Get нельзя передавать тело запроса, все параметры ограничены query-типом (url-параметры)
   "+" простата возможность сделать ссылку (с данными)
   "-" наглядно (просмотр данных, которые передаются), ограничение на объем данных

2. Post
  этим методом можно передавать как URL параметры, так и тело запроса
  req .GetParameter(name) проводит поиск в двух местах при наличии одинаковых
  имен - берется первое.
  "+"- возможность тела (больших объемов), данные спрятаны (от случайного просмотра)
  "-" - особенности построенной страницы методом post браузер выдает сообщение и патуется повторно отправить форму
   Решение этой проблемы также известной как сброс параметров выглядит следующим образом сервер на Пост - запрос посылает перенаправление
    (редирект), а дынные сохраняются в себе. Повторный запрос приходит без данных методом ГЕТ и это позволяет избежат-ь повторной передачи данных
    ПОСТ----> обработка сохранение                  | ? сохронение между запросами ?
    следование <------- редирект                    | HTTP-session - механизм такого сохранения
    (новый запрос ) ГЕТ ------> обновление данных,  |
    страница <--------- HTML
*/