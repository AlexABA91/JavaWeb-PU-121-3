<%@ page contentType="text/html;charset=UTF-8" %>
<div class="row container">
    <form action="" method="post">
        <div class="row valign-wrapper">
            <div class="input-field col s8 ">
                <input placeholder="Enter String for Hash" id="Hash" type="text" name="input_text" class="validate">

            </div>
            <div class="col-3">
                <button class="btn waves-effect waves-light " type="submit" value="send">Отправить
                    <i class="material-icons right">send</i>
                </button>

            </div>
        </div>
    <div class="row valign-wrapper">
        <h1 style="margin-bottom:15px"></h1>
        <input type="text" name="textResult"
               value="<%= request.getAttribute("hashString") == null ? "" : request.getAttribute("hashString") %>"
               readonly placeholder="Hash результат" />

        <button class="btn waves-effect waves-light" title="Download" type="submit" value="download" name="mode"
                <%if(request.getAttribute("hashString") == null|| request.getAttribute("hashString") == ""){%>
                disabled
                <%}%>>
            <i class="material-icons right">file_download</i>
        </button>
    </div>
    </form>
</div>