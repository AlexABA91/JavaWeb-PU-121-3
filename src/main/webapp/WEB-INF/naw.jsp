<%@ page contentType="text/html;charset=UTF-8"%>

<% String contextPath = request.getContextPath(); // база сайта - домашнее ссылка %>
<nav>
    <div class="nav-wrapper light-green darken-2 ">
        <a href="<%=contextPath%>" class="brand-logo">Logo</a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
            <li  <%if( ("jsp.jsp").equals(request.getParameter("pageName")) ) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/jsp">JSP</a>
            </li>
            <li <%if( ("aboutServlet.jsp").equals(request.getParameter("pageName")) ) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/aboutServlet">Servlet</a>
            </li>
            <li<%if( ("url.jsp").equals(request.getParameter("pageName")) ) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/url">URL</a>
            </li>
            <li<%if( ("url.jsp").equals(request.getParameter("pageName")) ) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/hash">Hash Form</a>
            </li>
        </ul>
    </div>
</nav>