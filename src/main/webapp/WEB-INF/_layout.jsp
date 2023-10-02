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
    <link rel="stylesheet" type="text/css" href="../<%=contextPath%>/css/style.css">
</head>
<body>

<header>
    <jsp:include page="nav.jsp">
        <jsp:param name="pageName" value="<%= pageName %>"/>
    </jsp:include>
</header>
<main>
    <img src="img/javaWeb.jpg" alt="javaWeb" class="floating left">
    <div class="container">
        <jsp:include page="<%= pageName  %>"/>
        <div id="confirm-email"></div>
    </div>
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

        <div class="modal-footer row">
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
    <!-- Modal Trigger -->

    <!-- Modal Structure -->
</main>
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
            onCloseStart: function () {
                loginInput.value = "";
                passwordInput.value = "";
                showToken.value = "";
            }
        });
        window.addEventListener("hashchange", frontRouter)
        frontRouter()
        setBtn()
    });

    function frontRouter() {
        console.log(location.hash)
        switch (location.hash) {
            case '#front':
                loadFrontPage()
                break;
            default:
        }
    }

    function setBtn() {
        const btn = document.getElementById("action-btn")
        if (!btn) throw "input id='action-btn' not find";

        if (btn) {
            const token = localStorage.getItem("webToken")
            if (!token) {
                btn.innerHTML = "<li>" +
                    "<a class='waves-effect waves-light btn modal-trigger #cddc39 lime'" +
                    "  title='Вход' href='#auth-modal'>" +
                    " <span class='material-icons'>login</span></a></li>";
            } else {
                btn.innerHTML = "<li>" +
                    "<a class='waves-effect waves-light btn modal-trigger #cddc39 red'" +
                    "   title='Выход' id='exit-btn'>" +
                    "<span class='material-icons'>logout</span></a></li>";

                document.getElementById("exit-btn").addEventListener("click", Exit)
            }
            console.log("btn found")
        }
    }

    document.getElementById("signIn").addEventListener("click", SingIn)


    function loadFrontPage() {
        const token = window.localStorage.getItem("webToken")
        //Проверяем есть ли токен в localStorage

        const headers = (token == null) ? {} : {'Authorization': `Bearer ${token}`}
        if (!token) {  // не авторизованный режим
            alert('Данная страница требует авторизации')
            window.location.href = "<%= contextPath%>"
            return;
        }
        // Пытаемся декодировать токен и пытаемся вычислить термин пригодности
        try {
            var data = JSON.parse(atob(token))

        } catch (ex) {
            alert('Токен не корректный повторите авторизацию')
            window.location.href = "<%= contextPath%>"
            return;
        }
        console.log(Date.parse(data.exp))
        console.log(Date.now())
        if (Date.parse(data.exp) < Date.now()) {
            alert("Токен просроченный повторите авторизацию")
            window.localStorage.removeItem("webToken")
        }
        const userAvatar = document.getElementById("avatar-user")
        if (!userAvatar) throw "avatar-user mot found"

        fetch("<%= contextPath %>/front", {
            method: "GET",
            headers: headers
        }).then(j => j.json()).then(j => {
            if (typeof j.login != "undefined") {

                userAvatar.innerHTML = `<img style="max-height:60px" src="<%= contextPath%>/upload/${j.avatar}" />`

                if (typeof j.emailConfirmCode == "string" &&
                    j.emailConfirmCode.length > 0) {
                    const confirmDiv = document.getElementById("confirm-email")
                    if (!confirmDiv) throw "confirm-code not found"
                    confirmDiv.innerHTML = `Почта не подтверждена. Введите код с е-листа
                 <div class="input-field inline"><input id='email-code'/></div>
                <button onclick='confirmCodeClick()'>Подтвердить</button>`;
                    confirmDiv.style.border = "1px solid maroon";
                    confirmDiv.style.padding = "5px 10px";
                    ;
                }
            }
            console.log(j)
        })
    }

    function confirmCodeClick() {

        const emailCodeInput = document.getElementById("email-code")
        if (!emailCodeInput) throw "email-code mot found"
        fetch("<%= contextPath %>/signup?code=" + emailCodeInput.value, {
            method: "PATCH",
            headers: {'Authorization': "Bearer " + window.localStorage.getItem("webToken")}
        }).then(r => {
            if (r.status === 202) {
                alert("Почта подтверждена")
                window.location.reload()
            } else {
                r.text().then(alert)
            }
        })

    }

    function Exit() {
        localStorage.removeItem("webToken")
        window.location.replace("<%=contextPath%>")
    }

    function SingIn() {
        const message = document.getElementById('message')
        if (!message) throw "input id='message' not find";
        const instance = M.Modal.getInstance(document.getElementById("auth-modal"));

        function ShowMessage(messageText) {

            message.classList.add("green-text", "text-darken-2")
            message.innerText = messageText
            setTimeout(function () {
                message.classList.remove("green-text", "text-darken-2")
                instance.close();
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

        fetch("<%= contextPath %>/signup", {
            method: "PUT",
            body: formData
        }).then(r => r.json()).then(
            (r) => {
                console.log(r.responseData)
                if (r.responseData.statusCode === 200) {
                    // декодируем токен узнаем даты

                    let token = JSON.parse(atob(r.base64))
                    console.log("Token expires " + token.exp)
                    window.localStorage.setItem("webToken", r.base64);
                    window.location.reload();
                } else {
                    ShowMessage(false, "Неправильный логин или пароль")
                }

            }
        )
    }
</script>
</body>

</html>
