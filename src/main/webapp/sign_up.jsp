<%-- 
    Document   : sign_up
    Created on : 2021.05.05
    Author     : chae
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!--adminhandler 이용하여 회원가입 하기 위하여 추가-->
<%@page import="cse.maven_webmail.control.CommandType" %>
<!DOCTYPE html>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>회원가입</title>
        
        <link type="text/css" rel="stylesheet" href="css/main_style.css" />
        
<script> <!--비밀번호와 비밀번호 확인 동일한지 검사-->
        function check_pw(){
            var pw = document.getElementById('password').value;
 
            if(pw.length < 6 || pw.length>20){
                window.alert('비밀번호는 6글자 이상, 20글자 이하만 이용 가능합니다.');
                document.getElementById('password').value='';
            }

            if(document.getElementById('password').value !='' && document.getElementById('repwd').value!=''){
                if(document.getElementById('password').value==document.getElementById('repwd').value){
                    document.getElementById('check').innerHTML='비밀번호가 일치합니다.'
                    document.getElementById('check').style.color='blue';
                }
                else{
                    document.getElementById('check').innerHTML='비밀번호가 일치하지 않습니다.';
                    document.getElementById('check').style.color='red';
                }
            }
        }
    </script>

     
    </head>
    <body>
        <jsp:include page="header.jsp" />
        <div align="center">
            <br />
            <!-- jsp page로 이동하는 것 대신 UserAdmin.do action 사용하여 회원가입 처리-->
             <form name="AddUser" action="UserAdmin.do?menu=<%= CommandType.ADD_USER_COMMAND%>"
                  method="POST">
            <table align="center" border="0">
                <tr>
                    <td align="center" valign="middle">
                        <table border="1" align="center" width ="600">
                               
                            <tr align="center">
                                <td colspan="3"><font color="#000000"><b>회원 가입</b></font></td>
                            </tr>


                            <tr>
                                <td width="20%">아이디</td>
                                <td width="50%"><input name="id" value="" id="id" size="20">
                                 
                            </tr>

                            <tr>
                                <td>비밀번호</td>
                                <td><input type="password" name="password"  value="" id="password" onchange="check_pw()"></td>
                            </tr>

                            <tr>
                                <td>비밀번호 확인</td>
                                <td><input type="password" name="password"  value="" id="repwd" onchange="check_pw()">&nbsp;<span id="check"></span></td>
                            </tr>

                            <tr><!-- inputCheck() -> script.js 연결해서 여러 오류 검사-->
                                <td colspan="3" align="center"><input type="submit"
                                                                      value="회원가입"> &nbsp; &nbsp; <input
                                                                      type="reset" value="다시쓰기"> &nbsp; &nbsp; <input
                                                                      type="button" value="취소"
                                                                      onClick="javascript:location.href = 'index.jsp'"></td>
                            </tr>
                            <tr>
                                회원가입시 모든 정보란에 입력 바랍니다. <br/>
                                ※비밀번호 주의사항: 6글자 이상, 20글자 이하<br/>
                       
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <jsp:include page="footer.jsp" />
</body>
</html>

