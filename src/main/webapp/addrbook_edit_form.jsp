<%--
  Created by IntelliJ IDEA.
  User: hwangjeongho
  Date: 2021/05/11
  Time: 10:09 오후
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="addrbook_error.jsp" import="cse.maven_webmail.*"%>

<jsp:useBean id="ab" scope="request" class="cse.maven_webmail.control.AddrBook" />

<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>주소록:작성화면</title>
    <link type="text/css" rel="stylesheet" href="css/main_style.css" />
</head>
<script language="javascript">
    function delcheck() {
        result = confirm("정말로 삭제하시겠습니까?");

        if (result) {
            document.form1.action.value = "delete";
            document.form1.submit();
        } else {
            return;
        }
    }
</script>
<body>
<div align="center">
    <H2>주소록:작성화면 </H2>
    <HR>
    [<a href=addrbook_control.jsp?action=list>주소록 목록으로</a>] <P>
    <form name=form1 method=post action=addrbook_control.jsp> <%--폼의 action은 컨트롤러로 설정 --%>
        <input type=hidden name="ab_id" value="<%=ab.getAb_id()%>">
        <input type=hidden name="action" value="insert">
        <table border="1">
            <tr>
                <th>이 름</th>
                <td><input type="text" name="ab_name" value="<%--<%=ab.getAb_name() %> --%>" ></td>
            </tr>
            <tr>
                <th>email</th>
                <td><input type="text" name="ab_email" value="<%--<%=ab.getAb_email() %> --%>" ></td>
            </tr>
            <tr>
                <th>전화번호</th>
                <td><input type="text" name="ab_tel" value="<%--<%=ab.getAb_tel() %>--%>" ></td>
            </tr>
            <tr>
                <th>생 일</th>
                <td><input type="date" name="ab_birth" value="<%--<%=ab.getAb_birth() %>--%>" ></td>
            </tr>
            <tr>
                <th>회 사</th>
                <td><input type="text" name="ab_comdept" value="<%--<%=ab.getAb_comdept() %>--%>" ></td>
            </tr>
            <tr>
                <th>메 모</th>
                <td><input type="text" name="ab_memo" value="<%--<%=ab.getAb_memo() %>--%>" ></td>
            </tr>
            <tr>
                <td colspan=2 align=center><input type=submit value="저장"><input type=reset value="취소"><input type="button" value="삭제" onClick="delcheck()"></td>
            </tr>
        </table>
    </form>

</div>
</body>
</html>