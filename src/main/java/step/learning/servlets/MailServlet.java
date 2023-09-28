package step.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import step.learning.services.hesh.HashService;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class MailServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // метод вызывается до того как происходит разделении по doXXX методам
        switch (req.getMethod().toUpperCase()){
            case "PATCH" :
                this.doPatch(req,resp);
                break;
            case "MAIL":
                this.doMail(req,resp);
                break;
            default:
                super.service(req, resp);
        }
    }

    protected void doMail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //zzem wvgt ouow jjud

        Properties emailProperties = new Properties();
        emailProperties.put("mail.smtp.auth","true");
        emailProperties.put("mail.smtp.starttls.enable","true");
        emailProperties.put("mail.smtp.port","587");
        emailProperties.put("mail.smtp.ssl.protocols","TLSv1.2");
        emailProperties.put("mail.smtp.ssl.trust","smtp.gmail.com");

        javax.mail.Session mailSession = Session.getInstance(emailProperties);
        mailSession.setDebug(true); // выводить в консоль процесс отправки почты

        try(Transport emailTransport = mailSession.getTransport("smtp")) {
            emailTransport.connect("smtp.gmail.com","alex1991020480@gmail.com","zzemwvgtouowjjud");
            // настраиваем сообщение
                javax.mail.internet.MimeMessage message = new MimeMessage( mailSession);
                message.setFrom(new InternetAddress("alex1991020480@gmail.com"));
                message.setSubject("From site JavaWeb");
              //  message.setContent("Поздравляем с регистрацией!","text/plain; charset=UTF-8" );
                message.setContent("<p><b>Поздравляем</b> с регистрацией! <a href='http://localhost:8080/JavaWeb_PU_121_3/'>на Сайте</a></p> ","text/html; charset=UTF-8" );
                 emailTransport.sendMessage(message, InternetAddress.parse("alex1991020481@gmail.com"));
                resp.getWriter().print("MAIL work");

            // отправляем его
        } catch (MessagingException e) {
            resp.getWriter().print(e.getMessage());
        }
    }
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // multipart сообщение - хранит несколько чястей с разным типом контента
        // (дял систем, которые не могут отображать все типы - выбегают доступный для себя)
        // также демонстрируем другой способ подключения
        Properties emailProperties = new Properties();
        emailProperties.put("mail.smtp.auth","true");
        emailProperties.put("mail.smtp.starttls.enable","true");
        emailProperties.put("mail.smtp.host","smtp.gmail.com");

        emailProperties.put("mail.smtp.port","587");
        emailProperties.put("mail.smtp.ssl.protocols","TLSv1.2");
        emailProperties.put("mail.smtp.ssl.trust","smtp.gmail.com");

        javax.mail.Session mailSession = Session.getInstance(emailProperties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("alex1991020480@gmail.com","zzemwvgtouowjjud");
                    }
                }
        );
        MimeMessage message = new MimeMessage(mailSession);
        try {
            message.setFrom(new InternetAddress("alex1991020480@gmail.com"));
            message.setSubject("Приветствие с сайта JavaWeb");
            message.setRecipients( // Получателей также перекладываем в сообщение
                    Message.RecipientType.TO,
                    InternetAddress.parse("alex1991020481@gmail.com")
            );
            MimeBodyPart html = new MimeBodyPart();
            html.setContent("<p><b>Поздравляем</b> с регистрацией! <a href='http://localhost:8080/JavaWeb_PU_121_3/'>на Сайте</a></p> ","text/html; charset=UTF-8" );
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent("Поздравляем с регистрацией!","text/plain; charset=UTF-8");
            // файловая часть (attachment)

            String resourceName ="javaWeb.jpg";
            // обращаемся в ресурсы в target/class
            String resPath =  this.getClass().getClassLoader().getResource(resourceName).getPath();
            MimeBodyPart filePart = new MimeBodyPart();
            filePart.setDataHandler(new DataHandler(new FileDataSource(resPath)));
            filePart.setFileName(resourceName);

            Multipart mailCon  = new MimeMultipart();
            mailCon.addBodyPart(html);
            mailCon.addBodyPart(filePart);
            mailCon.addBodyPart(textPart);


            message.setContent(mailCon);

            Transport.send(message);

            resp.getWriter().print("Multipart send");
            // отправляем его
        } catch (MessagingException e) {
            resp.getWriter().print(e.getMessage());
        }
    }
    @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            req.setAttribute("pageName","email");
            req.getRequestDispatcher("WEB-INF/_layout.jsp").forward(req,resp);
        }
}
/*
Работа с электронной почтой
    Па сколько послание электронной почты есть уязвимый инструментом, реализуем его не стандартным методом "MAIL"
* */