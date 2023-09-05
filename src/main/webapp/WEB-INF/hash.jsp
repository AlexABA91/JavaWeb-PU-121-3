<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="row container">
<form action="">
  <div class="input-field col s12">
    <input placeholder="Enter String" id="Hash" type="text" class="validate">
    <label for="Hash">Hash</label>
  </div>
  <button class="btn waves-effect waves-light" type="submit" name="action">Отправить
    <i class="material-icons right">send</i>
  </button>
</form>
  <div class="center-align">
    <h1 style="margin-bottom:15px">Hash строка</h1>
    <span ><%= request.getAttribute("hashString")%></span>
  </div>
</div>