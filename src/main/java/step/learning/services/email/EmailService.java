package step.learning.services.email;

import javax.mail.Message;

public interface EmailService {
    /**
     * Создание шаблону сообщения
     * @return  сообщение с заполненным Subject, Form и тд.
     * */
    Message prepareMassage(); // prepareMessage (String sample) - для разных шаблонов
    // ("с днем рождения", акционное предложение, подтверждение почты)

    /*
    Отправка "заполненного" сообщения
    @param message - дополненный шаблон сообщения
    **/
    void Send (Message message);
}
