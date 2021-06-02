<%@page import="cse.maven_webmail.control.MemberVO"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" errorPage="addrbook_error.jsp" %>
<link type="text/css" rel="stylesheet" href="css/main_style.css" />


<!DOCTYPE html PUBLIC
"-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Insert title here</title>
    <style>
        table {border: 2px double darkblue; width:666px}
        td,th{border:1px darkblue solid ; text-align: center; padding:5px}
    </style>
    <script>
        function idDelete(delID){

            //alert(delID);
            location.href = "addrbook_delete.jsp?id=" + delID;   //get방식으로 삭제할아이디를 넘김

        }


        function searchCheck(frm){
            //검색

            if(frm.keyWord.value ==""){
                alert("검색 단어를 입력하세요.");
                frm.keyWord.focus();
                return;
            }
            frm.submit();
        }
    </script>
</head>
<body>
<!--
    1. dao객체 선언한다.
    2. dao쪽의 select하는 메소드를 호출하여 그 결과를 리턴하여 테이블에 예쁘게 출력한다.
-->

<jsp:useBean id="dao" class="cse.maven_webmail.control.JDBCMemberDAO" />
<%

    request.setCharacterEncoding("UTF-8");
    String keyField = request.getParameter("keyField");
    String keyWord = request.getParameter("keyWord");
    ArrayList<MemberVO> list = dao.getMemberlist(keyField, keyWord);

    //ArrayList<MemberVO> list = dao.getMemberlist();
%>
<div align="center">
<h2>주소록:목록화면</h2>

<table border="1">
    <caption></caption>
        <th id="id_th">아이디</th><th id="name_th">이름</th><th id="tel_th">전화번호</th><th id="birth_th">생일</th><th id="addr_id">주소</th><th id="memo_th">메모</th><th id="nbsp_th">&nbsp;</th>
    <%
        for(MemberVO vo : list){
    %>

    <tr>
        <td><%=vo.getId() %></td>
        <td><%=vo.getName() %></td>
        <td><%=vo.getTel() %></td>
        <td><%=vo.getBirth() %></td>
        <td><%=vo.getAddr() %></td>
        <td><%=vo.getMemo()%>
        <td><input type="button" value="삭제" onclick="idDelete('<%=vo.getId() %>');"></td>

    </tr>
    <%
        }
    %>

    <tr>
        <td colspan="7"> <br/>
            <form name="serach" method ="post">
                <select name="keyField">
                    <option value="0"> ----선택----</option>
                    <option value="id">아이디</option>
                    <option value="name">이름</option>
                    <option value="addr">주소</option>
                </select>
                <input type="text" name="keyWord" />
                <input type="button" value="검색" onclick="searchCheck(form)" />

            </form>

        </td>
    </tr>


    <!-- ------------------------------------------------------------------------------ -->
    <tr>
        <td colspan="7"><p align="right"><a href="addrbook_select.jsp">[전체목록]</a> | <a href="addrbook_form.html">[회원가입]</a> </p></td>
    </tr>

</table>
</div>
</body>
</html>
