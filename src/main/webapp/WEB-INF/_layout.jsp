<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String pageName =                     // Достаем значение атрибута (заложенный в сервлете)
            (String)                     // так как - значение объект нужно прямое превращение
                    request                       // объект request доступен во всех JSP независимо от сервлетов
                            .getAttribute(
                                    "pageName" // прямое совпадение с переменной необязательно
                            ) + ".jsp";
    // Параметры можно модифицировать
    String contextPath = request.getContextPath();
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
    <meta charset="UTF-8">
    <link rel="stylesheet" href="<%= pageName %>/css/style.css">
</head>
<body>


<jsp:include page="naw.jsp">
 <jsp:param name="pageName" value="<%= pageName %>" />
</jsp:include>
<img src="img/javaWeb.jpg" alt="javaWeb" class="floating left">
<div class="container">
    <jsp:include page="<%= pageName  %>"/>
</div>
<!-- Modal Trigger -->

<!-- Modal Structure -->
<div id="auth-modal" class="modal">
    <div class="modal-content">
        <h4>Аутентификация</h4>
        <div class="row">
            <form class="col s12">
                <div class="row">
                    <div class="input-field col s10">
                        <i class="material-icons prefix">account_circle</i>
                        <input id="aut-login" type="text" class="validate">
                        <label for="aut-login">Логин</label>
                    </div>
                    <div class="input-field col s10">
                        <i class="material-icons prefix">mode_edit</i>
                        <input id="aut-password" type="password" class="validate">
                        <label for="aut-password">Пароль</label>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="modal-footer">
        <a href="<%=contextPath%>/signup" class="modal-close waves-effect #4dd0e1 cyan lighten-2 btn-flat">Регистрация</a>
        <a href="#!" class="modal-close waves-effect #ff7043 deep-orange lighten-1 btn-flat">Забыл пароль</a>
        <a href="#!" class="modal-close waves-effect #aeea00 lime accent-4 btn-flat">Вход</a>
    </div>
</div>
    <jsp:include page="footer.jsp"/>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        var elems = document.querySelectorAll('.modal');
        var instances = M.Modal.init(elems, {
            opacity:0.5
        });
    });

    $(document).ready(function(){
        $('.modal').modal();
    });
</script>
</body>

</html>
