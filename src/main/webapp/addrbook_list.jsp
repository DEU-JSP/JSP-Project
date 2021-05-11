<%--
  Created by IntelliJ IDEA.
  User: hwangjeongho
  Date: 2021/05/11
  Time: 10:06 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="addrbook_error.jsp" import="java.util.*, cse.maven_webmail.*"%>
<!DOCTYPE HTML>
<html>
<head>

    <%--스타일 시트 적용 부분 /addrbook.css 파일을 불러와 스타일 적용 --%>
    <link rel="stylesheet" href="addrbook.css" type="text/css" media="screen" />

    <script type="text/javascript">
        function check(ab_id) {
            pwd = prompt('수정/삭제 하려면 비밀번호를 넣으세요');
            document.location.href="addrbook_control.jsp?action=edit&ab_id="+ab_id+"&upasswd="+pwd;
        }
    </script>

    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>주소록:목록화면</title>

</head>
<jsp:useBean id="datas" scope="request" class="java.util.ArrayList" />

<body>
<div align="center">
    <H2>주소록:목록화면</H2>
    <HR>
    <form>
        <a href="addrbook_edit_form.jsp">주소록 등록</a><P>

        <table border="1">
            <tr><th>번호</th><th>이 름</th><th>전화번호</th><th>생 일</th><th>회 사</th><th>메 모</th></tr>
            <%
                for(AddrBook  ab : (ArrayList<AddrBook>)datas) {
            %>
              <tr>
               <td><a href="javascript:check(<%=ab.getAb_id()%>)"><%=ab.getAb_id() %></a></td>
                <td><%=ab.getAb_tel() %></td>
                <td><%=ab.getAb_name() %></td>
                <td><%=ab.getAb_birth() %></td>
                <td><%=ab.getAb_comdept() %></td>
                <td><%=ab.getAb_memo() %></td>
              </tr>
             <%
                }
             %>

            <%-- 화면구조확인 위해 --%>
            <tr>
                <td>
                    <%--주소록 번호 클릭시 수정화면으로 이동 / 추후 자바스크립트를 통해 비밀번호 입력 후 컨트롤러로 연결하도록 수정 --%>
                    <a href="addrbook_edit_form.jsp">1</a></td><td>강호동</td><td>010-123-1234</td><td>1998-10-12</td>
                <td>동의대학교</td><td>ICT융합대학</td>
            </tr>

            <tr>
                <td>2</a></td><td>김영화</td><td>010-3333-3333</td><td>1997-01-13</td>
                <td>동의대학교</td><td>ICT융합대학</td>
            </tr>

            <tr>
                <td>3</a></td><td>전규빈</td><td>010-2222-2222</td><td>1997-09-30</td>
                <td>동의대학교</td><td>ICT융합대학</td>
            </tr>

        </table>
    </form>

</div>
</body>
</html>
