<%-- 
    Document   : deleteusers
    Created on : 2021. 5. 16., 오후 7:33:37
    Author     : chae
--%>

<%@tag description="admin_menu tag file" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix ="sql"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="mytags" %>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="user" required="true"%>
<%@attribute name="password" required="true"%>
<%@attribute name="schema" required="true"%>
<%@attribute name="table" required="true"%>

<sql:setDataSource var = "dataSrc"
                   url="jdbc:mysql://172.20.16.1:3306/${schema}?serverTimezone=Asia/Seoul"
                   driver="com.mysql.cj.jdbc.Driver"
                   user="${user}" password="${password}"/>

<sql:query var="rs" dataSource="${dataSrc}">
    SELECT username FROM ${table}
</sql:query>
${empty errorReason ?"<noerror/>" : errorReason}

<script type="text/javascript">
    <!--
function getConfirmResult() {
        var result = confirm("사용자를 정말로 삭제하시겠습니까?");
        return result;
}
-->
</script>
    
<table border="1">
    <thead>
        <tr>
            <th>아이디</th>
            <th>삭제</th>
        </tr>
    </thead>
    <tbody>
    <c:forEach var="row" items="${rs.rows}">
        <tr>
            <td>${row.username}</td>
            <td>
                <form name="DeleteUser" action="Delete" method="POST";>
                    <input type-="checkbox" name="username" value="${row.username}" onClick ="return getConfirmResult()" style="visibility: false;" checked="checked"
                           <input type="submit" value="삭제"></td>
                </form>
        </tr>
    </c:forEach>
</tbody>
</table>
