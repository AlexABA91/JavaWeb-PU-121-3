<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" %>

    <h1>Основы JSP</h1>
    <p><b>JSP</b></p> - (Java Server Pages) -технология веб разработки, аналогия ASP или PHP.
    <p>
        Какие возможности обычно добавляют к HTML средства препроцессинга
        - переменные <br/>
        - Выражения (вычисления)<br/>
        - условные операции (условная верстка)<br/>
        - цикловые конструкции <br/>
        - композиция (подключение отдельных файлов)
    </p>
    <p>
        Код Java вставляется в любое место JSP-файла с помощью конструкции
        &lt;% %&gt;
        <%
            int x = 10;
            String str = "Hello";
        %>
        Вставка в HTML - &lt;%= %&gt;
        x = <%= x %> str+ "world" = <%= str + "World"%>
    </p>
    <p>
        Условные конструкции создаются так <br/>
        &lt;% if(condition){%&gt;<br/>
        Тело условного блока (как HTML так и вставка кода )<br/>
        &lt;}%&gt;<br/>
        <% if (x < 10) { %>
        <b> меньше чем 10</b>
        <% } else {%>
        <i>x больше или ровно 10</i>
        <%}%>
    </p>
    <p>
        Цикл
        &lt;% цикл (){%&gt;
        &nbsp; тело цикла
        &lt;% }%&gt; <br/>
        <% for (int i = 0; i < 10; i++) {%>
        <b><%= i + 1 %>
        </b>.<i> index: <%= i%>
    </i><br/>
        <%}%>
    </p>
    <table>
        <thead>
        <tr>
            <th>№</th>
            <th>Название </th>
        </tr>
        </thead>
        <tbody>
        <% String[] arr = new String[]{"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"}; %>
        <% for (int i = 0; i < arr.length; ++i) {%>
        <tr>
            <td><b><%=i%>.</b></td>
            <td><i><%=arr[i]%>
            </i></td>
        </tr>
        <%}%>
        </tbody>
    </table>
