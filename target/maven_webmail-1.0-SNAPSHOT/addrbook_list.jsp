<%--
  Created by IntelliJ IDEA.
  User: hwangjeongho
  Date: 2021/05/11
  Time: 10:06 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="addrbook_error.jsp" import="java.util.*, cse.maven_webmail.*"%>
<%@ page import="cse.maven_webmail.control.AddrBook" %>
<!DOCTYPE HTML>
<html>
<head>

    <%--스타일 시트 적용 부분 /addrbook.css 파일을 불러와 스타일 적용 --%>
    <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    <!--
    <script type="text/javascript">
        function check(ab_id) {
            pwd = prompt('수정/삭제 하려면 비밀번호를 넣으세요');
            document.location.href="addrbook_control.jsp?action=edit&ab_id="+ab_id+"&upasswd="+pwd;
        }
    </script>
    -->
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
            <tr><th>번호</th><th>사용자 ID</th><th>전화번호</th><th>생 일</th><th>회 사</th><th>메 모</th><th>삭 제</th></tr>
            <%
                for(AddrBook ab : (ArrayList<AddrBook>)datas) {
            %>
              <tr>
               <td><a href="javascript:check(<%=ab.getAb_id()%>)"><%=ab.getAb_id() %></a></td>
                <td><%=ab.getAb_name() %></td>
                <td><%=ab.getAb_tel() %></td>
                <td><%=ab.getAb_birth() %></td>
                <td><%=ab.getAb_comdept() %></td>
                <td><%=ab.getAb_memo() %></td>
                  <td><form action="addrbook_modify.jsp" method="post">삭제수정할 번호
                      <input type=text name="<%=ab.getAb_id()%>"><button type = "submit">삭제/수정</button></form></td>>
              </tr>
             <%
                }
             %>

        </table>
        <br>
        <a href="main_menu.jsp">이전 메뉴로</a><P>
    </form>

</div>
</body>
</html>
