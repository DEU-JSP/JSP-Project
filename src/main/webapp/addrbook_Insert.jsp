<%--
  Created by IntelliJ IDEA.
  User: hwangjeongho
  Date: 2021/05/27
  Time: 1:32 오전
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="addrbook_error.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<link type="text/css" rel="stylesheet" href="css/main_style.css" />

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
</head>
<body>
<!--
    1. 넘어오은 값 한글 인코딩 처리한다.
    2. 사용할 객체 useBean한다
    3. setXxx를 호출하여 넘어오는 값들 저장한다.
    3. dao쪽의 insert하는 메소드 호출하여 성공 여부를 리턴한후
       성공하면 addrbook_select.jsp 이동 실패하면 뒤로 이동 시킨다.
 -->

<%request.setCharacterEncoding("UTF-8");%>

    <jsp:useBean id="vo" class="cse.maven_webmail.control.MemberVO" />
    <jsp:setProperty property="*" name="vo"/>
    <jsp:useBean id="dao" class="cse.maven_webmail.control.JDBC_memberDAO"/>

<%
    if(dao.memberInsert(vo)>0){

        out.print("<script>");
        out.print("alert('가입을 축하드립니다.');");
        out.print("location.href='addrbook_select.jsp';");
        out.print("</script>");
    }else{

        out.print("<script>");
        out.print("alert('회원가입이 정상적으로 완료되지 않았습니다.');");
        out.print("history.back();");
        out.print("</script>");
    }
%>

</body>
</html>