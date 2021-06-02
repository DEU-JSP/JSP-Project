<%-- 
    Document   : sidebar_menu.jsp
    Author     : jongmin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandTypeHelper" %>

<!DOCTYPE html>

<html lang="ko">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>웹메일 시스템 메뉴</title>
    </head>
    <body>
        <br> <br>
        
        <span style="color: indigo"> <strong>사용자: <%= session.getAttribute("userid") %> </strong> </span> <br>
        <p> <a href="addrbook_select.jsp"> 주소록 </a></p>
        <p> <a href="main_menu.jsp"> 메일 읽기 </a> </p>
        <p> <a href="write_mail.jsp"> 메일 쓰기 </a> </p>
        <p> <a href="write_mail_me.jsp"> 내게 쓰기 </a></p>
        <p><a href="trash.jsp">휴지통</a></p>
        <p><a href="Login.do?menu=<%= CommandTypeHelper.LOGOUT %>">로그아웃</a></p>
    </body>
</html>
