<%--
  Created by IntelliJ IDEA.
  User: hwangjeongho
  Date: 2021/05/11
  Time: 11:43 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html lang="ko">
<head>
<title>WEBMAIL SYSTEM</title>
</head>
<body>
    <link type="text/css" rel="stylesheet" href="css/main_style.css" />

<div>
    <H2>주소록 에러</H2>
    <HR>
    <table border-collapse=5 width=400>
        <caption></caption>
        <tr width=100% bgcolor="pink">
            <th id="info_th"></th>
            <td>
            주소록 처리중 에러가 발생 했습니다.<BR>
            관리자에게 문의해 주세요..<BR>
            빠른시일내 복구하겠습니다.
            문의사항이생기시면<a>hwangjeongho@kakao.com</a>
            <HR>
            에러내용 : <%= exception%>
            <HR>

        </td>
        </tr>
    </table>
</div>
</body>
</html>
