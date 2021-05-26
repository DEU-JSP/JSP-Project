<%--
  Created by IntelliJ IDEA.
  User: hwangjeongho
  Date: 2021/05/27
  Time: 1:40 오전
  To change this template use File | Settings | File Templates.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="addrbook_error.jsp"%>
<link type="text/css" rel="stylesheet" href="css/main_style.css" />
<!DOCTYPE html PUBLIC
"-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>
<body>
<jsp:useBean id="dao" class="cse.maven_webmail.control.JDBC_memberDAO" />

<%
    //넘어온 ID값 받아서 삭제하는 메소드 호출

    int result = dao.delMemberlist(request.getParameter("id"));

    String message="삭제되지 않았습니다.";
    if(result > 0 ){

        message = "삭제되었습니다.";
    }
%>

<script>
    alert("<%=message%>");
    location.href="addrbook_select.jsp";
</script>
</body>
</html>
