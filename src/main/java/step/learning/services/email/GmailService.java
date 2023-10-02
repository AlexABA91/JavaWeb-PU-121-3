package step.learning.services.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class GmailService implements EmailService {
    @Override
    public Message prepareMassage() {
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
            message.setFrom(
                    new InternetAddress("alex1991020480@gmail.com"));

            message.setSubject("Приветствие с сайта JavaWeb");

        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return message;
    }

    @Override
    public void Send(Message message) {
        try{
        Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
