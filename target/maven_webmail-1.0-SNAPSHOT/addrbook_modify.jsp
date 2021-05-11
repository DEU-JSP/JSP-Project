<%--
  Created by IntelliJ IDEA.
  User: hwangjeongho
  Date: 2021/05/11
  Time: 11:34 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="addrbook_error.jsp" import="cse.maven_webmail.*"%>
<!DOCTYPE HTML>
<html>
<head>
    <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    <script type="text/javascript">
        function delcheck() {
            result = confirm("정말로 삭제하시겠습니까 ?");

            if(result == true){
                document.form1.action.value="delete";
                document.form1.submit();
            }
            else
                return;
        }
    </script>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>주소록:수정화면</title>
</head>

<%-- 자바 빈 추가 부분
<jsp:useBean id="ab" scope="request" class="jspbook.addrbook.AddrBook" />
 --%>

<body>
<div align="center">
    <H2>주소록:수정화면 </H2>
    <HR>
    [<a href=addrbook_control.jsp?action=list>주소록 목록으로</a>] <p>
    <form name=form1 method=post action=addrbook_control.jsp>
        <%-- DB연동 후 추가부분 <input type=hidden name="ab_id" value="<%=ab.getAb_id()%>">--%>
        <input type=hidden name="action" value="update">

        <table border="1">
            <tr>
                <th>이 름</th>
                <td><input type="text" name="ab_name" value="강호동<%--<%=ab.getAb_name() %> --%>"></td>
            </tr>
            <tr>
                <th>email</th>
                <td><input type="text" name="ab_email" value="kdh@devil.com<%--<%=ab.getAb_email() %> --%>"></td>
            </tr>
            <tr>
                <th>전화번호</th>
                <td><input type="text" name="ab_tel" value="010-1234-1234<%--<%=ab.getAb_tel() %> --%>"></td>
            </tr>
            <tr>
                <th>생 일</th>
                <td><input type="date" name="ab_birth" value="1998-10-02<%--<%=ab.getAb_birth() %> --%>"></td>
            </tr>
            <tr>
                <th>학 교</th>
                <td><input type="text" name="ab_comdept" value="동의대학교<%--<%=ab.getAb_comdept() %> --%>"></td>
            </tr>
            <tr>
                <th>메 모</th>
                <td><input type="text" name="ab_memo" value="ICT공과대학<%--<%=ab.getAb_memo() %> --%>"></td>
            </tr>
            <tr>
                <td colspan=2 align=center><input type=submit value="저장"><input type=reset value="취소"><input type="button" value="삭제" onClick="delcheck()"></td>
            </tr>
        </table>
    </form>

</div>
</body>
</html>