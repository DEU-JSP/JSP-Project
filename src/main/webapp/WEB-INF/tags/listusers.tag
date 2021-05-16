<%-- 
    Document   : listusers
    Created on : 2021. 5. 15., 오후 4:33:22
    Author     : chae
--%>

<%@tag description="admin_menu tag file" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/sql" prefix ="sql"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix ="c"%>

<%-- The list of normal or fragment attributes can be specified here: --%>
<%@attribute name="user" required="true"%>
<%@attribute name="password" required="true"%>
<%@attribute name="schema" required="true"%>
<%@attribute name="table" required="true"%>

<sql:setDataSource var = "dataSrc"
                   url="jdbc:mysql://172.29.80.1:3306/${schema}?serverTimezone=Asia/Seoul"
                   driver="com.mysql.cj.jdbc.Driver"
                   user="${user}" password="${password}"/>

<sql:query var="rs" dataSource="${dataSrc}">
    SELECT username FROM ${table}
</sql:query>
        ${empty errorReason ?"<noerror/>" : errorReason}
        
<table border="1">
    <thead>
        <tr>
            <th>아이디</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="row" items="${rs.rows}">
            <tr>
                <td>${row.username}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>