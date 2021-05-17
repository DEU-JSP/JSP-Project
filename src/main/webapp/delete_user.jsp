<%-- 
    Document   : delete_user.jsp
    Author     : chae
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandType" %>
<%@page import="cse.maven_webmail.model.UserAdminAgent" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@taglib tagdir="/WEB-INF/tags" prefix="mytags" %>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>사용자 제거 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <div id="sidebar">
            <%-- 사용자 추가때와 동일하므로 같은 메뉴 사용함. --%>
            <jsp:include page="sidebar_admin_previous_menu.jsp" />
        </div>

        <div id="main">
            <h2> 삭제할 사용자를 선택해 주세요. </h2> <br>

            <!-- 아래 코드는 위와 같이 Java Beans와 JSTL을 이용하는 코드로 바꾸어져야 함 -->
            <%
                String cwd = this.getServletContext().getRealPath(".");
            %>

            <c:catch var="errorReason">
                <mytags:deleteusers user="james" password="chaechae"
                                    schema="webmail" table="users" />
            </c:catch>
            ${empty errorReason ? "<noerror/>" : errorReason} 
            <br>
        </div>

        <jsp:include page="footer.jsp" />
    </body>
</html>
