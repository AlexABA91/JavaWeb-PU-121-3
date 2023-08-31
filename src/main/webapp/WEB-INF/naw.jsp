<%@ page contentType="text/html;charset=UTF-8"%>
<nav>
    <div class="nav-wrapper light-green darken-2 ">
        <a href="index.jsp" class="brand-logo">Logo</a>
        <ul id="nav-mobile" class="right hide-on-med-and-down">
            <li  <%if( ("jsp.jsp").equals(request.getParameter("pageName")) ) {%> class="active"<%}%>>
                <a href="jsp">JSP</a>
            </li>
            <li <%if( ("aboutServlet.jsp").equals(request.getParameter("pageName")) ) {%> class="active"<%}%>>
                <a href="aboutServlet">Servlet</a>
            </li>
            <li<%if( ("url.jsp").equals(request.getParameter("pageName")) ) {%> class="active"<%}%>>
                <a href="url">URL</a>
            </li>
        </ul>
    </div>
</nav>