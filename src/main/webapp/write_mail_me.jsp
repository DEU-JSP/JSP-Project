<%-- 
    Document   : write_mail.jsp
    Author     : Dayoung Kim
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="cse.maven_webmail.control.CommandTypeHelper" %>

<!DOCTYPE html>

<%-- @taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" --%>
<style type="text/css">
    .put{
        background-color: #c8fbc4;
        border:none;
        color: #c8fbc4;
    }
</style>

<html lang="ko">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>메일 쓰기 화면</title>
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
    </head>
    <body>
        <jsp:include page="header.jsp" />

        <div id="sidebar">
            <jsp:include page="sidebar_previous_menu.jsp" />
        </div>

        <div id="main">
            <form enctype="multipart/form-data" method="POST"
                  action="WriteMail.do?menu=<%= CommandTypeHelper.SEND_MAIL_COMMAND%>" >
                <table>
                    <caption>
                    </caption>
                    <tr>
                        <th id="self_th"></th>
                        <td colspan="2">&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;내게&nbsp;&nbsp;쓰기
                            <input type="text" name="to" size="8" value="<%=session.getAttribute("userid")%>" class="put"></td>
                    </tr>
                    <tr>
                        <th id="paste_th"></th>
                        <td>참조</td>
                        <td> <input type="text" name="cc" size="80">  </td>
                    </tr>
                    <tr>
                        <th id="title_th"></th>
                        <td> 메일 제목 </td>
                        <td> <input type="text" name="subj" size="80"  >  </td>
                    </tr>
                    <tr>
                        <th id="body_th"></th>
                        <td colspan="2">본  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 문</td>
                    </tr>
                    <tr>  <%-- TextArea    --%>
                        <td colspan="2">  <textarea rows="15" name="body" cols="80"></textarea> </td>
                    </tr>
                    <tr>
                        <th id="file_th"></th>
                        <td>첨부 파일</td>
                        <td> <input type="file" name="file1"  size="80">  </td>
                    </tr>
                    <tr>
                        <th id="other_th"></th>
                        <td colspan="2">
                            <input type="submit" value="내게 전송">
                            <input type="reset" value="다시 입력">
                        </td>
                    </tr>
                </table>
            </form>
        </div>

        <jsp:include page="footer.jsp" />
    </body>
</html>
