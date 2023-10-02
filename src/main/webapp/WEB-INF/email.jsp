
<%String contextPath = request.getContextPath();%>
<%@ page contentType="text/html;charset=UTF-8" %>
<h1>Работа с электронной почтой </h1>
<div class="row">
    <div class="col s3 ">
        <button class="waves-effect waves-light btn" onclick="textEmailClock()">Отправить Text<i class="material-icons right">send</i></button>
    </div>
    <div class="col s3">
        <button class="waves-effect waves-light btn" onclick="htmlEmailClock()">Отправить HTML<i class="material-icons right">send</i></button>
    </div>
    <div class="col s3">
        <button class="waves-effect waves-light btn" onclick="serviceEmailClock()">Отправить Service<i class="material-icons right">send</i></button>
    </div>
</div>
<div class="row">
    <div class="col s12 m8 offset-m2 l6 offset-l3">
        <div class="card-panel grey lighten-5 z-depth-1">
            <div class="row valign-wrapper">
                <div class="col s2">
                    <i class="material-icons right">send</i>
                </div>
                <div class="col s10">
              <span class="black-text" id="email-res">
               Статус сообщения будет отображен после нажатия на одну из кнопок отправки
              </span>
                </div>
            </div>
        </div>
    </div>
</div>
<p>
    Для отправки почты необходимо настроить smtp (Simple MAil Transfer protocol)<br/>
    Необходима регистрация электронной почты, сервис которой позволяет SMPT в бесплатной версии<br/>
    на примере GMAIL это требует включение двух факторной аутентификации<br/>
    <strong>!!!!! используйте приватную почту !!!!!</strong><br/>
</p>
<script>
    function serviceEmailClock() {
        fetch("<%=contextPath%>/email",{
            method: "LINK"
        }).then(r=>r.text()).then(t=>{
            document.getElementById("email-res").innerText = t;
        })
}

function htmlEmailClock() {
    fetch("<%=contextPath%>/email",{
        method: "PATCH"
    }).then(r=>r.text()).then(t=>{
        document.getElementById("email-res").innerText = t;
    })
}

function textEmailClock(){
        fetch("<%=contextPath%>/email",{
            method: "MAIL"
        }).then(r=>r.text()).then(t=>{
           document.getElementById("email-res").innerText = t;
        })
    }
</script>
