package step.learning.services.db;

import java.sql.Connection;

public interface DbProvider {
    Connection getConnection();
}

/*
Добавление базы данных у web проекту
 - организация службы, поскольку работа с бд возможна с разных точек проекта
 - Сокрытия параметров логин/пароль от публикации на репозитории
 - Жизненный цикл подключение (регистрации драйвера - работа - удаление драйвера)
*/