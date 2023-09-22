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
    <link rel="stylesheet" href="<%= contextPath %>/css/style.css">
</head>
<body>


<jsp:include page="nav.jsp">
    <jsp:param name="pageName" value="<%= pageName %>"/>
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
            <div class="col s12">
                <div class="row">
                    <div class="input-field col s10">
                        <i class="material-icons prefix">account_circle</i>
                        <input id="aut-login" type="text" name="login" class="validate">
                        <label for="aut-login">Логин</label>
                    </div>
                    <div class="input-field col s10">
                        <i class="material-icons prefix">mode_edit</i>
                        <input id="aut-password" type="password" name="password" class="validate">
                        <label for="aut-password">Пароль</label>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div  class="modal-footer row">
        <div class="col s6">
            <span id="message"></span>
            <ul id="WebToken"></ul>
        </div>
        <div class="col s6">
            <a href="<%=contextPath%>/signup" class="modal-close waves-effect #4dd0e1 cyan lighten-2 btn-flat">Регистрация</a>
            <a href="#!" class="modal-close waves-effect #ff7043 deep-orange lighten-1 btn-flat">Забыл пароль</a>
            <button class="waves-effect #aeea00 lime accent-4 btn-flat" id="signIn">Вход</button>
        </div>
    </div>
</div>
<jsp:include page="footer.jsp"/>
<script>
    const loginInput = document.getElementById('aut-login')
    if (!loginInput) throw "input id= 'aut-login' not find";
    const passwordInput = document.getElementById('aut-password')
    if (!passwordInput) throw "input id='aut-password' not find";
    const showToken = document.getElementById("WebToken")
    if (!showToken) throw "input id='showToken' not find";

    document.addEventListener('DOMContentLoaded', function () {
        var elems = document.querySelectorAll('.modal');
        var instances = M.Modal.init(elems, {
            opacity: 0.5,
            onCloseStart : function (){
                loginInput.value = "";
                passwordInput.value ="";
                showToken.value="";
            }
        });
        window.addEventListener("hashchange",frontRouter)
        frontRouter()
    });
    function frontRouter(){
        console.log(location.hash)
        switch (location.hash) {
            case '#front':
                    loadFrontPage()
                break;
            default:
        }
    }

    document.getElementById("signIn").addEventListener("click", SingIn)

    function loadFrontPage(){
        const token = window.localStorage.getItem("webToken")
        const headers = (token == null )?{}:{ 'Authorization':`Bearer ${token}`}
        fetch("<%= contextPath %>/front",{
            method:"GET",
            headers: headers
        }).then(r=>r.text()).then(t=>console.log(t))
    }

    function SingIn() {

        const message = document.getElementById('message')
        if (!message) throw "input id='message' not find";
        const instance = M.Modal.getInstance(document.getElementById("auth-modal"));

        function ShowMessage(isSuccess, messageText) {
            if (!isSuccess) {
                message.classList.add("red-text", "text-darken-2")

            } else {
                message.classList.add("green-text", "text-darken-2")
            }
            message.innerText = messageText
            setTimeout(function () {
                if (!isSuccess) {
                    message.classList.remove("red-text", "text-darken-2")

                } else {
                    message.classList.remove("green-text", "text-darken-2")
                    instance.close();
                }
                message.innerText = ""
            }, 3000)
        }

        if (loginInput.value.trim().length < 2) {
            ShowMessage(false, "Введите логин")
            return;
        }
        if (passwordInput.value.length < 2) {
            ShowMessage(false, 'введите пароль')
            return;
        }
        const formData = new FormData()
        formData.append("aut-login", loginInput.value)
        formData.append("aut-password", passwordInput.value)
        // const  data = {
        //       login:loginInput.value,
        //       password: passwordInput.value
        //  }
        fetch("<%= contextPath %>/signup", {
            method: "PUT",
            body: formData
            // JSON.stringify(data)
        }).then(r => r.json()).then(
            (r) => {
                console.log(r.responseData)
                if(r.responseData.statusCode === 200){
                    window.localStorage.setItem("webToken", r.base64 );
                    ShowMessage(true, "Вход выполнен успешно")
                }else{
                    ShowMessage(false, "Неправильный логин или пароль")
                }
                // if (r.responseData.statusCode === 200) {
                //     ShowMessage(true, "Вход успешен")
                //     // r.webToken.
                //     showToken.innerHTML += "<li style='color: yellow'>" + "ID: <span style='color: red'>" + r.webToken.id +  "</span></li>"
                //     showToken.innerHTML += "<li style='color: black'>" + "Sub: <span style='color: red'>" + r.webToken.sub + "</span></li>"
                //     showToken.innerHTML += "<li style='color: green'>" + "iat: <span style='color: red'>" + r.webToken.iat + "</span></li>"
                //     showToken.innerHTML += "<li  style='color: violet'>" + "Exp:<span style='color:red'>" + r.webToken.exp +"</span></li>"
                //     showToken.innerHTML += "<li  style='color: violet'>" + "Exp:<span style='color:red'>" + r.base64 +"</span></li>"
                // } else {
                //     ShowMessage(false, "Неправильный логин или пароль")
                // }
            }
        )
    }
    </script>
</body>

</html>
