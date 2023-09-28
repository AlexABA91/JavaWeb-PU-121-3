<%@ page contentType="text/html;charset=UTF-8" %>

<% String contextPath = request.getContextPath(); // база сайта - домашнее ссылка %>

<nav>

    <div class="nav-wrapper light-green darken-2 ">

        <a href="<%=contextPath%>" class="brand-logo logo offset-s1">
            JavaWeb
        </a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
            <li><a href="#front">Front</a></li>

            <li  <%if (("jsp.jsp").equals(request.getParameter("pageName"))) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/jsp">JSP</a>
            </li>
            <li <%if (("aboutServlet.jsp").equals(request.getParameter("pageName"))) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/aboutServlet">Servlet</a>
            </li>
            <li<%if (("url.jsp").equals(request.getParameter("pageName"))) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/url">URL</a>
            </li>
            <li<%if (("url.jsp").equals(request.getParameter("pageName"))) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/hash">Hash Form</a>
            </li>
            <li<%if (("url.jsp").equals(request.getParameter("pageName"))) {%> class="active"<%}%>>
                <a href="<%=contextPath%>/email">Email</a>
            </li>
            <li>
                <a class="waves-effect waves-light btn modal-trigger #cddc39 lime " title="Вход" href="#auth-modal">
                    <span class="material-icons">login</span>
                </a>
            </li>
            <li id="avatar-user">

            </li>
        </ul>
    </div>
</nav>