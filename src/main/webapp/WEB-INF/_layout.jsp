<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String pageName =                     // Достаем значение атрибута (заложенный в сервлете)
            (String)                     // так как - значение объект нужно прямое превращение
                    request                       // объект request доступен во всех JSP независимо от сервлетов
                            .getAttribute(
                                    "pageName" // прямое совпадение с переменной необязательно
                            ) + ".jsp";          // Параметры можно модифицировать
%>
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
    <link rel="stylesheet" href="<%= pageName %>/css/style.css">
</head>
<body>

<jsp:include page="naw.jsp">

    <jsp:param name="pageName" value="<%= pageName %>" />
</jsp:include>
<div class="container">
    <jsp:include page="<%= pageName  %>"/>
</div>
<jsp:include page="footer.jsp"/>
</body>
</html>
