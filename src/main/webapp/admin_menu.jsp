<%-- 
    Document   : admin_menu.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.model.UserAdminAgent"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@taglib tagdir="/WEB-INF/tags" prefix="mytags" %>

<!DOCTYPE html>

<html lang="ko">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>사용자 관리 메뉴</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <div id="sidebar">
            <jsp:include page="sidebar_admin_menu.jsp" />
        </div>

        <div id="main">
            <h2> 메일 사용자 목록 </h2>
            <!-- 아래 코드는 위와 같이 Java Beans와 JSTL을 이용하는 코드로 바꾸어져야 함 -->
        </div>
        <c:catch var="errorReason">
            <mytags:listusers user="james" password="chaechae"
                              schema="webmail" table="users" />
        </c:catch>
        ${empty errorReason ? "<noerror/>" : errorReason} 
        <jsp:include page="footer.jsp" />
    </body>
</html>
