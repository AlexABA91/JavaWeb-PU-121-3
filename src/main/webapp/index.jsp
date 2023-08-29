<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!--Import materialize.css-->
    <link type="text/css" rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css"
          media="screen,projection"/>

    <!--Let browser know website is optimized for mobile-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
</head>
<body>

<jsp:include page="naw.jsp"/>

<h1>Java Web</h1>
<p>
    Создаем новый проект, Maven archetype -- ... -webapp
</p>
<p>
    Настраиваем конфигурацию запуска Веб-проект запускается через веб-сервер, который не входит
    в коробку Java и нуждается в отдельной установки.
    Для этого существуют:
</p>
<ul>
    <li>Apache Tomcat (для JAva-8 -- До версии 9, рекомендованною 8)</li>
    <li>Glassfish(4 или 5)</li>
    <li>Jboss/ WildFly(до 20)</li>
    <li>GAE - Google app Engine и другие облачные сервисы</li>
</ul>
<p>
    Скачиваем любой сервер в большинстве случаем достаточно
    распаковать из архива отдельной распаковать не потребуется
</p>
<p>
    Edit Configuration -- add -- Tomcat local -- указываем парку сервера (куда распакованный сервер )
    Устанавливаем артефакт для выгрузки (deploy) -- ..war exploded
    По желанию изменяем контекст артефакта (не обязательно)
    ОК
</p>
<p>
    Запускаем, при наличии сбоя кодирования добавляем в сам проект на начало файла &lt; @page contentType =
    "text/html;charset=UTF-8" language="java"
    в заголовок HTML
</p>
<p>
    Деплой проекта выглядит следующим
    - происходит сборка (...war_exploded - web archive)
    - Этот архив переносится в специальную папку на которую смотрит сервер
    - сервер при первом обращении к сайту распаковывает архив и работает с его файлами <br/>
</p>
<p>ВАРИАНТЫ ПЕРЕЗАПУСКА :</p>
<ul>
    <li>Update resources - изменить в папке деплоя только статические ресурсы (html, css, ...)</li>
    <li>Update classes and resources - перекомпилировать классы и ресурсы с ними связанные</li>
    <li> Redeploy - повторить процедуру деплоя</li>
    <li>Restart Server - остановить сервер и запустить заново и процедуру деплоя тоже</li>
</ul>

<jsp:include page="footer.jsp"/>
</body>
</html>
