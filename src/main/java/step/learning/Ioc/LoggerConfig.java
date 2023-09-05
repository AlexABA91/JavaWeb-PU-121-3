package step.learning.Ioc;

import com.google.inject.AbstractModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class LoggerConfig extends AbstractModule {
    @Override
    protected void configure() {
        try(InputStream propertiesStream = this.getClass().getClassLoader().getResourceAsStream("logging.properties"
                // Обращаемся к ресурсу. Папка Resources есть часть проекта. .getResourceStream обращяемся к ней
        )) {
            LogManager logManager = LogManager.getLogManager();
            logManager.reset();
            logManager.readConfiguration(propertiesStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } ;

    }
}

/*
Настройка логера (для Guis)
- в папку resoursces создаем/копируем файл logger.properties (он есть частью JDK/JRE образец можно найти в папках устаноки java или в интернете )
- в клвссе конфигурации передаем содержимое этого файла в натройи логерп
- добовляем этот клссс к конфигурации Guice
*/
