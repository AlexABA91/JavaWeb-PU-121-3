package step.learning.Ioc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

import javax.servlet.ServletContextEvent;

public class IocListener extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(
           new FilterConfig(),
           new ServletConfig(),
           new LoggerConfig(),
           new ServiceConfig()
        );
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);
    }
}
/*
Инверсия управления в WEB проектах(Google Guice к web проектам)
1 Зависимости (пакеты) : pom.xml
 -  <!-- https://mvnrepository.com/artifact/com.google.inject/guice -->
 (проверенно на версии 7 0 0 )
 -  <!-- https://mvnrepository.com/artifact/com.google.inject.extensions/guice-servlet -->
 (берем 6,0,0 для совмещения с javax)
2 Настройка WEB.xml
- убираем все настройки сервлетов и фильтров(Будут перенесенные в конфигурацию Guice)
- добовляем филтр Guece и наш слушатель события создания контекста(старта веб работы)
-- некий аналог MEIN
3 создаем конфиг-класы (один или нескольео), указываем фильтры и сервлеты
!! для всех класов фильтров и сервлетов нужно указать @Singleton И убрать @WebServlet

*/