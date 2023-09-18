<%@ page contentType="text/html;charset=UTF-8" %>
<h2>Регистрация нового пользователя</h2>
<%
    String[] genders = {
            "Мурской",
            "Женский",
            "Не Указывать"
    };
    String[] culture = {"uk-UA", "en-US","ru-RU","en-CA", "fr-CA", "fr-FR", "de-DE", "it-IT","es-ES","en-GB","ja-JP"};
%>
<div class="container" style="margin-top: 10%; width: 80%">
    <div class="card-panel grey lighten-5">
        <div class="row">
            <div class="row">
                <div class="input-field col s6">
                    <i class="material-icons prefix">account_circle</i>
                    <input id="reg-name" name="reg-name" type="text" class="validate">
                    <label for="reg-name">Имя</label>
                </div>
                <div class="input-field col s6">
                    <i class="material-icons prefix">account_circle</i>
                    <input id="reg-lastname" name="reg-lastname" type="text" class="validate">
                    <label for="reg-lastname">Фамилия</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s6">
                    <i class="material-icons prefix">email</i>
                    <input id="reg-email" name="reg-email" type="text" class="validate">
                    <label for="reg-email">Почта</label>
                </div>
                <div class="input-field col s6">
                    <i class="material-icons prefix">phone_iphone</i>
                    <input id="reg-phone" name="reg-phone" type="text" class="validate">
                    <label for="reg-phone">Телефон</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s6">
                    <i class="material-icons prefix">child_friendly</i>
                    <input id="reg-birthdate" name="reg-birthdate" type="date" class="validate">
                    <label for="reg-birthdate">День рождения</label>
                </div>
                <div class="file-field input-field col s6">
                    <div class="btn">
                        <span>File</span>
                        <input type="file" id="reg-avatar"  name="reg-avatar">
                    </div>
                    <div class="file-path-wrapper">
                        <input class="file-path validate" type="text"  placeholder="Выберете аватар">
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s6">
                    <i class="material-icons prefix">badge</i>
                    <input id="reg-login" name="reg-login" type="text" class="validate">
                    <label for="reg-login">Логин</label>
                </div>
                <div class="input-field col s6">
                    <i class="material-icons prefix">lock</i>
                    <input id="reg-password" name="reg-password" type="password" class="validate">
                    <label for="reg-password">Пароль</label>
                </div>
            </div>
            <div class="row">
                <div class="input-field col s6">
                    <i class="material-icons prefix">translate</i>
                    <select name="reg-culture" id="reg-culture" >
                        <option value="" disabled selected>Выберете культуру</option>
                        <% for (int i=0; i<culture.length;i++) { %>
                        <option value="<%=culture[i]%>"><%=culture[i]%></option>
                        <% } %>
                    </select>
                    <label>Локализация</label>
                </div>
                <div class="input-field col s6">
                    <i class="material-icons prefix">traffic</i>
                    <select id="reg-gender" name="reg-gender">
                        <option value="" disabled selected>Выберите пол</option>
                        <% for (int i=0; i<genders.length;i++) { %>
                        <option value="<%=genders[i]%>"><%=genders[i]%></option>
                        <% } %>
                    </select>
                    <label>Гендер</label>
                </div>
            </div>
            <button style="color: white; margin-top: 5%; width: 100%" class="btn waves-effect waves-light offset-s1 #9e9d24 lime darken-3 " type="submit">
                <i class="material-icons right">check</i>Регистрация
            </button>
        </div>
    </div>
</div>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        var elems = document.querySelectorAll('select');
        M.FormSelect.init(elems, {});

        const signupButton = document.querySelector('button[type="submit"]');
        if(signupButton){
            signupButton.addEventListener('click',signupClick)
        }else  {
            console.error('signupClick not find')
        }
    });
    function signupClick(){
        console.log("sending...")
        const nameInput = document.getElementById('reg-name')
        if(! nameInput) throw "input id= 'reg-name' not find";
        const lastNameInput = document.getElementById('reg-lastname')
        if(! lastNameInput) throw "input id='reg-lastname' not find";
        const emailInput = document.getElementById('reg-email')
        if(! emailInput) throw "input id='reg-email' not find";
        const phoneInput = document.getElementById('reg-phone')
        if(! phoneInput) throw "input id='reg-phone' not find";
        const birthdayInput = document.getElementById('reg-birthdate')
        if(! birthdayInput) throw "input id='reg-birthdate' not find";
        const avatarInput = document.getElementById('reg-avatar')
        if( ! avatarInput ) throw "input id='reg-avatar' not found" ;
        const loginInput = document.getElementById('reg-login');
        if( ! loginInput ) throw "input id='reg-login' not found" ;
        const passwordInput = document.getElementById('reg-password');
        if( ! passwordInput ) throw "input id='reg-password' not found" ;
        const cultureInput = document.getElementById('reg-culture');
        if( ! cultureInput ) throw "input id='reg-culture' not found" ;
        const genderInput = document.getElementById('reg-gender');
        if( ! genderInput ) throw "input id='reg-gender' not found" ;

        const formData =new FormData();
        if(emailInput.value.trim().length<2){
            alert('Электронная почта обязательна')
            return;
        }
        formData.append(emailInput.name,emailInput.value);

        if(loginInput.value.trim().length<2){
            alert('Логин короткое или не введенное')
            return;
        }
        formData.append(loginInput.name,loginInput.value);

        if(passwordInput.value.trim().length<2){
            alert('пароль короткое или не введен')
            return;
        }
        formData.append(passwordInput.name,passwordInput.value );
        formData.append(nameInput.    name,nameInput.value     );
        formData.append(lastNameInput.name,lastNameInput.value );
        formData.append(phoneInput.   name,phoneInput.value    );
        formData.append(birthdayInput.name,birthdayInput.value );
        formData.append(cultureInput. name,cultureInput.value  );
        formData.append(genderInput.  name,genderInput.value   );
        formData.append(avatarInput.name,avatarInput.files[0]);
        fetch(window.location.href,{
            method:"POST",
            body:formData
        }).then(r=>r.text()).then(console.log)

        console.log(formData)
    }
</script>